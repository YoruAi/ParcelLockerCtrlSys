package service;

import dao.FrameDao;
import dao.JDBCFrameDao;
import pojo.Frame;

import java.util.List;

public class FrameService {
    private static final FrameDao frameDao = new JDBCFrameDao();

    public static void storeFrame(Frame frame) {
        frameDao.insertFrame(frame);
    }

    public static List<Frame> loadFrames(String portName) {
        return frameDao.queryFrames(portName);
    }
}
