package yoru.parcellockerctrlsysweb.mapper;

import org.apache.ibatis.annotations.Mapper;
import yoru.parcellockerctrlsysweb.pojo.Frame;

import java.util.List;

@Mapper
public interface FrameMapper {
    void insertFrame(Frame frame);

    List<Frame> queryFrames(String portName);

    Long countFrames(String portName);

    List<Frame> queryFramesByPage(String portName, int start, Integer pageSize);
}
