#ifndef VIDEOHANDLER_H
#define VIDEOHANDLER_H

extern "C" {
    #include <libavcodec/avcodec.h>
    #include <libavformat/avformat.h>
    #include <libavutil/opt.h>
    #include <libavutil/imgutils.h>
}

#include <iostream>
#include <string>
#include "Logger.h"  // Подключаем наш класс Logger

class VideoHandler {
private:
    std::string rtsp_url;
    std::string rtp_url;
    Logger logger;  // Объект для логирования

public:
    VideoHandler(const std::string& rtsp, const std::string& rtp)
        : rtsp_url(rtsp), rtp_url(rtp), logger("video_logs_db", "video_logs") {
        logger.log(Logger::INFO, "VideoHandler initialized with RTSP: " + rtsp + " and RTP: " + rtp);
    }

    void startStreaming() {
        avformat_network_init(); // Инициализация сети для RTSP
        logger.log(Logger::INFO, "Network initialized for RTSP streaming.");

        AVFormatContext* inputFormatCtx = nullptr;
        AVFormatContext* outputFormatCtx = nullptr;

        // Открываем RTSP поток
        if (avformat_open_input(&inputFormatCtx, rtsp_url.c_str(), nullptr, nullptr) != 0) {
            logger.log(Logger::ERROR, "Failed to open RTSP stream: " + rtsp_url);
            return;
        }
        logger.log(Logger::INFO, "RTSP stream opened successfully: " + rtsp_url);

        if (avformat_find_stream_info(inputFormatCtx, nullptr) < 0) {
            logger.log(Logger::ERROR, "Failed to retrieve stream information from RTSP.");
            avformat_close_input(&inputFormatCtx);
            return;
        }
        logger.log(Logger::INFO, "Stream information retrieved from RTSP.");

        // Создаём контекст для RTP
        avformat_alloc_output_context2(&outputFormatCtx, nullptr, "rtp", rtp_url.c_str());
        if (!outputFormatCtx) {
            logger.log(Logger::ERROR, "Failed to create RTP context.");
            avformat_close_input(&inputFormatCtx);
            return;
        }
        logger.log(Logger::INFO, "RTP context created successfully.");

        // Копируем параметры потоков
        for (unsigned int i = 0; i < inputFormatCtx->nb_streams; i++) {
            AVStream* inStream = inputFormatCtx->streams[i];
            AVStream* outStream = avformat_new_stream(outputFormatCtx, nullptr);
            if (!outStream) {
                logger.log(Logger::ERROR, "Failed to create RTP stream.");
                avformat_close_input(&inputFormatCtx);
                avformat_free_context(outputFormatCtx);
                return;
            }
            avcodec_parameters_copy(outStream->codecpar, inStream->codecpar);
        }
        logger.log(Logger::INFO, "Stream parameters copied to RTP context.");

        // Открываем RTP поток
        if (avio_open(&outputFormatCtx->pb, rtp_url.c_str(), AVIO_FLAG_WRITE) < 0) {
            logger.log(Logger::ERROR, "Failed to open RTP connection: " + rtp_url);
            avformat_close_input(&inputFormatCtx);
            avformat_free_context(outputFormatCtx);
            return;
        }
        logger.log(Logger::INFO, "RTP connection opened successfully: " + rtp_url);

        if (avformat_write_header(outputFormatCtx, nullptr) < 0) {
            logger.log(Logger::ERROR, "Failed to write RTP headers.");
            avformat_close_input(&inputFormatCtx);
            avio_close(outputFormatCtx->pb);
            avformat_free_context(outputFormatCtx);
            return;
        }
        logger.log(Logger::INFO, "RTP headers written successfully.");

        AVPacket pkt;
        while (av_read_frame(inputFormatCtx, &pkt) >= 0) {
            pkt.stream_index = 0;
            pkt.pts = av_rescale_q(pkt.pts, inputFormatCtx->streams[0]->time_base, outputFormatCtx->streams[0]->time_base);
            pkt.dts = pkt.pts;

            if (av_interleaved_write_frame(outputFormatCtx, &pkt) < 0) {
                logger.log(Logger::ERROR, "Failed to write frame to RTP.");
                break;
            }
            av_packet_unref(&pkt);
        }

        logger.log(Logger::INFO, "Streaming finished. Writing trailer and cleaning up.");
        av_write_trailer(outputFormatCtx);
        avformat_close_input(&inputFormatCtx);
        avio_close(outputFormatCtx->pb);
        avformat_free_context(outputFormatCtx);
        avformat_network_deinit();
    }
};

#endif // VIDEOHANDLER_H