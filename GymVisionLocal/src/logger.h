#ifndef LOGGER_H
#define LOGGER_H

#include <mongocxx/client.hpp>
#include <mongocxx/instance.hpp>
#include <mongocxx/uri.hpp>
#include <bsoncxx/json.hpp>
#include <iostream>
#include <chrono>
#include <iomanip>
#include <sstream>
#include <stdexcept>

class Logger {
public:
    // Уровни логирования
    enum LogLevel { INFO, WARNING, ERROR };

    // Конструктор
    Logger(const std::string& db_name, const std::string& collection_name)
        : instance_{}, client_{mongocxx::uri{"mongodb://localhost:27017"}} {
        db_ = client_[db_name];
        collection_ = db_[collection_name];
    }

    // Метод для записи лога
    void log(LogLevel level, const std::string& message) {
        try {
            auto log_entry = bsoncxx::builder::stream::document{}
                << "timestamp" << get_current_time()
                << "level" << log_level_to_string(level)
                << "message" << message
                << bsoncxx::builder::stream::finalize;

            collection_.insert_one(log_entry.view());
        } catch (const std::exception& e) {
            std::cerr << "Failed to insert log: " << e.what() << std::endl;
        }
    }

private:
    mongocxx::instance instance_;  // Инициализация MongoDB C++ Driver
    mongocxx::client client_;      // Клиент для подключения к MongoDB
    mongocxx::database db_;        // База данных
    mongocxx::collection collection_;  // Коллекция

    // Получение текущего времени в формате строки
    std::string get_current_time() {
        auto now = std::chrono::system_clock::now();
        auto in_time_t = std::chrono::system_clock::to_time_t(now);

        std::stringstream ss;
        ss << std::put_time(std::localtime(&in_time_t), "%Y-%m-%d %X");
        return ss.str();
    }

    // Преобразование уровня логирования в строку
    std::string log_level_to_string(LogLevel level) {
        switch (level) {
            case INFO: return "INFO";
            case WARNING: return "WARNING";
            case ERROR: return "ERROR";
            default: throw std::invalid_argument("Invalid log level");
        }
    }
};

#endif //LOGGER_H
