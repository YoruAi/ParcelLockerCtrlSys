package dao;

import pojo.DeviceStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JDBCDeviceStatusDao implements DeviceStatusDao {
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/db_parcel_locker";
    private final String username = "root";
    private final String password = "111111";

    // 插入设备新状态
    public void insertDeviceStatus(DeviceStatus deviceStatus) {
        String sql = "INSERT INTO tb_device_status (device_code, device_address, system_status, " +
                "compressor_status, current_temperature, set_temperature, lock_status, status_upload_interval, " +
                "compressor_startup_delay, temperature_deviation, time, port_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deviceStatus.getDeviceCode());
            stmt.setInt(2, deviceStatus.getDeviceAddress());
            stmt.setByte(3, deviceStatus.getSystemStatus());
            stmt.setByte(4, deviceStatus.getCompressorStatus());
            stmt.setDouble(5, deviceStatus.getCurrentTemperature());
            stmt.setDouble(6, deviceStatus.getSetTemperature());
            stmt.setShort(7, deviceStatus.getLockStatus());
            stmt.setInt(8, deviceStatus.getStatusUploadInterval());
            stmt.setInt(9, deviceStatus.getCompressorStartupDelay());
            stmt.setInt(10, deviceStatus.getTemperatureDeviation());
            stmt.setTimestamp(11, Timestamp.valueOf(deviceStatus.getTime()));
            stmt.setString(12, deviceStatus.getPortName());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(LocalDateTime.now() + " - 向数据库保存设备状态数据成功！");
            }
        } catch (SQLException e) {
            System.err.println(LocalDateTime.now() + " - 向数据库保存设备状态数据失败: " + e.getSQLState());
        }
    }

    // 查询指定串口设备的最新状态
    public DeviceStatus queryLatestDeviceStatus(String portName) {
        String sql = "SELECT * FROM tb_device_status WHERE port_name = ? ORDER BY time DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, portName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
                return mapToDeviceStatus(rs);
        } catch (SQLException e) {
            System.err.println(LocalDateTime.now() + " - 向数据库查询设备状态数据失败: " + e.getSQLState());
        }
        return new DeviceStatus();
    }

    // 查询历史设置温度列表
    public List<Double> queryHistorySetTemperature(String portName) {
        String sql = "SELECT set_temperature FROM tb_device_status WHERE port_name = ? ORDER BY time";

        List<Double> temperatures = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, portName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                temperatures.add(rs.getDouble("set_temperature"));
            }
        } catch (SQLException e) {
            System.err.println(LocalDateTime.now() + " - 向数据库查询历史设置温度数据失败: " + e.getSQLState());

        }
        return temperatures;
    }

    // 查询历史采集温度列表
    public List<Double> queryHistoryCurrentTemperature(String portName) {
        String sql = "SELECT current_temperature FROM tb_device_status WHERE port_name = ? ORDER BY time";

        List<Double> temperatures = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, portName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                temperatures.add(rs.getDouble("current_temperature"));
            }
        } catch (SQLException e) {
            System.err.println(LocalDateTime.now() + " - 向数据库查询历史采集温度数据失败: " + e.getSQLState());
        }
        return temperatures;
    }

    private DeviceStatus mapToDeviceStatus(ResultSet rs) throws SQLException {
        DeviceStatus status = new DeviceStatus();
        status.setDeviceCode(rs.getString("device_code"));
        status.setDeviceAddress(rs.getInt("device_address"));
        status.setSystemStatus(rs.getByte("system_status"));
        status.setCompressorStatus(rs.getByte("compressor_status"));
        status.setCurrentTemperature(rs.getDouble("current_temperature"));
        status.setSetTemperature(rs.getDouble("set_temperature"));
        status.setLockStatus(rs.getShort("lock_status"));
        status.setStatusUploadInterval(rs.getInt("status_upload_interval"));
        status.setCompressorStartupDelay(rs.getInt("compressor_startup_delay"));
        status.setTemperatureDeviation(rs.getInt("temperature_deviation"));
        status.setTime(rs.getTimestamp("time").toLocalDateTime());
        status.setPortName(rs.getString("port_name"));
        return status;
    }
}