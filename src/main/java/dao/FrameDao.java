package dao;

import pojo.Frame;

import java.util.List;

public interface FrameDao {
    void insertFrame(Frame frame);

    List<Frame> queryFrames(String portName);
}
