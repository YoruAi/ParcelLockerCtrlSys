DROP DATABASE IF EXISTS db_parcel_locker;

CREATE DATABASE db_parcel_locker;

USE db_parcel_locker;

CREATE TABLE `tb_device_status` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `device_code` char(10) NOT NULL COMMENT '设备编码',
    `device_address` int NOT NULL COMMENT '控制板号',
    `system_status` tinyint unsigned NOT NULL COMMENT '系统状态',
    `compressor_status` tinyint unsigned NOT NULL COMMENT '压缩机状态',
    `current_temperature` float NOT NULL COMMENT '当前温度',
    `set_temperature` float NOT NULL COMMENT '设置温度',
    `lock_status` smallint NOT NULL COMMENT '锁状态',
    `status_upload_interval` int NOT NULL COMMENT '状态上传间隔（单位：秒）',
    `compressor_startup_delay` int NOT NULL COMMENT '压缩机启动延时（单位：秒）',
    `temperature_deviation` int NOT NULL COMMENT '温度控制偏差',
    `time` timestamp NOT NULL COMMENT '时刻',
    `port_name` varchar(10) NOT NULL COMMENT '串口名字',
    PRIMARY KEY (`id`),
    KEY `tb_device_status_device_address_index` (`device_address`),
    KEY `tb_device_status_time_index` (`time`)
) ENGINE=InnoDB AUTO_INCREMENT=10049 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `tb_frames` (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
     `direction` tinyint NOT NULL COMMENT '方向（0收；1发）',
     `length` int NOT NULL COMMENT '长度',
     `frame_number` int NOT NULL COMMENT '帧号',
     `device_address` int NOT NULL COMMENT '设备地址',
     `type` tinyint NOT NULL COMMENT '类型',
     `data` blob COMMENT '数据',
     `check_code` smallint NOT NULL COMMENT 'CRC',
     `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '帧时刻',
     `port_name` varchar(10) NOT NULL COMMENT '串口名字',
     PRIMARY KEY (`id`),
     KEY `tb_frames_time_index` (`time`)
) ENGINE=InnoDB AUTO_INCREMENT=12497 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# delete from tb_device_status where 1;
# delete from tb_frames where 1;