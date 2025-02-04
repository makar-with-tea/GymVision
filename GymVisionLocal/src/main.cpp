#include <grpcpp/grpcpp.h>
#include <thread>
#include <mutex>
#include <unordered_map>
#include "generated/video_streaming.grpc.pb.h"  // Сгенерированный gRPC-код
#include "Logger.h"
#include "OnvifCamera.h"
#include "VideoHandler.h"

using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;
using grpc::Status;
using video_streaming::StreamRequest;
using video_streaming::StreamResponse;
using video_streaming::VideoStreaming;

// Глобальные переменные для управления потоками
std::unordered_map<std::string, std::thread> active_streams;  // Активные потоки трансляции
std::mutex mtx;  // Мьютекс для синхронизации доступа к active_streams
Logger logger("streaming_logs_db", "streaming_logs");

// Класс gRPC-сервиса
class VideoStreamingServiceImpl final : public VideoStreaming::Service {
public:
    Status StartStream(ServerContext* context, const StreamRequest* request, StreamResponse* response) override {
        std::string camera_ip = request->camera_ip();
        std::string camera_username = request->camera_username();
        std::string camera_password = request->camera_password();
        std::string rtp_destination = request->rtp_destination();

        logger.log(Logger::INFO, "Received StartStream request for camera: " + camera_ip + ", destination: " + rtp_destination);

        // Проверяем, не запущена ли уже трансляция для этой камеры
        {
            std::lock_guard<std::mutex> lock(mtx);
            if (active_streams.find(camera_ip) != active_streams.end()) {
                response->set_success(false);
                response->set_message("Stream is already running for this camera.");
                logger.log(Logger::WARNING, "Stream is already running for camera: " + camera_ip);
                return Status::OK;
            }
        }

        // Запускаем трансляцию в отдельном потоке
        std::thread stream_thread([camera_ip, camera_username, camera_password, rtp_destination]() {
            logger.log(Logger::INFO, "Starting stream for camera: " + camera_ip);

            // Создаем объект OnvifCamera для получения RTSP-ссылки
            OnvifCamera camera(camera_ip, camera_username, camera_password);
            std::string rtsp_url = camera.getRTSPUrl();

            if (rtsp_url.empty()) {
                logger.log(Logger::ERROR, "Failed to get RTSP URL for camera: " + camera_ip);
                return;
            }

            // Создаем объект VideoHandler для передачи видеопотока
            VideoHandler video_handler(rtsp_url, rtp_destination);
            video_handler.startStreaming();

            // Удаляем поток из активных после завершения трансляции
            {
                std::lock_guard<std::mutex> lock(mtx);
                active_streams.erase(camera_ip);
            }

            logger.log(Logger::INFO, "Stream finished for camera: " + camera_ip);
        });

        // Добавляем поток в активные
        {
            std::lock_guard<std::mutex> lock(mtx);
            active_streams[camera_ip] = std::move(stream_thread);
        }

        response->set_success(true);
        response->set_message("Stream started successfully.");
        logger.log(Logger::INFO, "Stream started for camera: " + camera_ip);
        return Status::OK;
    }
};

// Функция для запуска gRPC-сервера
void RunServer() {
    std::string server_address("0.0.0.0:50051");
    VideoStreamingServiceImpl service;

    ServerBuilder builder;
    builder.AddListeningPort(server_address, grpc::InsecureServerCredentials());
    builder.RegisterService(&service);

    std::unique_ptr<Server> server(builder.BuildAndStart());
    logger.log(Logger::INFO, "gRPC server listening on " + server_address);

    server->Wait();
}

int main() {
    // Запускаем gRPC-сервер в отдельном потоке
    std::thread grpc_server_thread(RunServer);

    // Основной поток может выполнять другие задачи
    while (true) {
        std::this_thread::sleep_for(std::chrono::seconds(1));
    }

    grpc_server_thread.join();
    return 0;
}