package dao;

import pojo.Frame;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JDBCFrameDao implements FrameDao {
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/db_parcel_locker";
    private final String username = "root";
    private final String password = "111111";

    @Override
    public void insertFrame(Frame frame) {
        String sql = "INSERT INTO tb_frames (direction, length, frame_number, device_address, " +
                "type, data, check_code, time, port_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, frame.getDirection());
            pstmt.setInt(2, frame.getLength());
            pstmt.setInt(3, frame.getFrameNumber());
            pstmt.setInt(4, frame.getDeviceAddress());
            pstmt.setByte(5, frame.getType());

            pstmt.setBytes(6, frame.getData());

            pstmt.setShort(7, frame.getCheckCode());
            pstmt.setTimestamp(8, Timestamp.valueOf(frame.getTime()));
            pstmt.setString(9, frame.getPortName());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(LocalDateTime.now() + " - 向数据库保存帧数据成功！");
            }

        } catch (SQLException e) {
            System.err.println(LocalDateTime.now() + " - 向数据库保存帧数据失败: " + e.getSQLState());
        }
    }

    @Override
    public List<Frame> queryFrames(String portName) {
        List<Frame> frames = new ArrayList<>();
        String sql = "SELECT * FROM tb_frames WHERE port_name=?";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, portName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Frame frame = new Frame();
                    frame.setDirection(rs.getInt("direction"));
                    frame.setLength(rs.getInt("length"));
                    frame.setFrameNumber(rs.getInt("frame_number"));
                    frame.setDeviceAddress(rs.getInt("device_address"));
                    frame.setType(rs.getByte("type"));

                    Blob dataBlob = rs.getBlob("data");
                    frame.setData(dataBlob.getBytes(1, (int) dataBlob.length()));

                    frame.setCheckCode(rs.getShort("check_code"));
                    Timestamp timestamp = rs.getTimestamp("time");
                    frame.setTime(timestamp.toLocalDateTime());
                    frame.setPortName(rs.getString("port_name"));

                    frames.add(frame);
                }
            }
        } catch (SQLException e) {
            System.err.println(LocalDateTime.now() + " - 向数据库查询帧数据失败: " + e.getSQLState());
        }

        return frames;
    }
}
