#ifndef ONVIFCAMERA_H
#define ONVIFCAMERA_H

#include <iostream>
#include <string>
#include "soapStub.h"          // Заголовки gSOAP
#include "soapMediaBindingProxy.h"
#include "soapPTZBindingProxy.h"
#include "wsseapi.h"           // Для аутентификации
#include "Logger.h"            // Подключаем наш класс Logger

class OnvifCamera {
private:
    std::string camera_ip;
    std::string username;
    std::string password;
    std::string media_service_url;
    std::string ptz_service_url;
    Logger logger;  // Объект для логирования

    // Функция для аутентификации WSSE (UsernameToken)
    void addAuth(struct soap* soap) {
        soap_wsse_add_UsernameTokenDigest(soap, nullptr, username.c_str(), password.c_str());
    }

public:
    OnvifCamera(const std::string& ip, const std::string& user, const std::string& pass)
        : camera_ip(ip), username(user), password(pass), logger("onvif_logs_db", "onvif_logs") {
        media_service_url = "http://" + camera_ip + "/onvif/media";
        ptz_service_url = "http://" + camera_ip + "/onvif/ptz";
        logger.log(Logger::INFO, "OnvifCamera initialized with IP: " + ip + ", user: " + user);
    }

    // Метод получения RTSP ссылки
    std::string getRTSPUrl() {
        MediaBindingProxy media;
        struct soap* soap = soap_new();
        addAuth(soap);  // Добавляем аутентификацию

        _trt__GetStreamUri request;
        _trt__GetStreamUriResponse response;

        request.StreamSetup = new tt__StreamSetup();
        request.StreamSetup->Stream = tt__StreamType__RTP_Unicast;
        request.StreamSetup->Transport = new tt__Transport();
        request.StreamSetup->Transport->Protocol = tt__TransportProtocol__RTSP;
        request.ProfileToken = "Profile_1";

        logger.log(Logger::INFO, "Attempting to get RTSP URL from camera: " + camera_ip);

        if (media.GetStreamUri(media_service_url.c_str(), nullptr, &request, response) == SOAP_OK) {
            std::string rtsp_url = response.MediaUri->Uri;
            logger.log(Logger::INFO, "RTSP URL retrieved successfully: " + rtsp_url);
            soap_destroy(soap);
            soap_end(soap);
            soap_free(soap);
            return rtsp_url;
        } else {
            logger.log(Logger::ERROR, "Failed to retrieve RTSP URL from camera: " + camera_ip);
            soap_print_fault(soap, stderr);
            soap_destroy(soap);
            soap_end(soap);
            soap_free(soap);
            return "";
        }
    }

    // Метод управления PTZ движением
    void moveCamera(float pan, float tilt) {
        PTZBindingProxy ptz;
        struct soap* soap = soap_new();
        addAuth(soap);  // Добавляем аутентификацию

        _tptz__ContinuousMove request;
        _tptz__ContinuousMoveResponse response;

        request.ProfileToken = "Profile_1";
        request.Velocity = new tt__PTZSpeed();
        request.Velocity->PanTilt = new tt__Vector2D();
        request.Velocity->PanTilt->x = pan;
        request.Velocity->PanTilt->y = tilt;
        request.Velocity->PanTilt->space = nullptr;

        logger.log(Logger::INFO, "Attempting to move camera: pan=" + std::to_string(pan) + ", tilt=" + std::to_string(tilt));

        if (ptz.ContinuousMove(ptz_service_url.c_str(), nullptr, &request, response) != SOAP_OK) {
            logger.log(Logger::ERROR, "Failed to move camera: pan=" + std::to_string(pan) + ", tilt=" + std::to_string(tilt));
            soap_print_fault(soap, stderr);
        } else {
            logger.log(Logger::INFO, "Camera moved successfully: pan=" + std::to_string(pan) + ", tilt=" + std::to_string(tilt));
        }

        soap_destroy(soap);
        soap_end(soap);
        soap_free(soap);
    }

    // Обёртки для поворота камеры
    void moveLeft()  { moveCamera(-0.5,  0.0); }
    void moveRight() { moveCamera( 0.5,  0.0); }
    void moveUp()    { moveCamera( 0.0,  0.5); }
    void moveDown()  { moveCamera( 0.0, -0.5); }
};

#endif // ONVIFCAMERA_H