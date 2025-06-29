package yoru.parcellockerctrlsysweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yoru.parcellockerctrlsysweb.mapper.FrameMapper;
import yoru.parcellockerctrlsysweb.pojo.Frame;
import yoru.parcellockerctrlsysweb.pojo.FrameResponse;
import yoru.parcellockerctrlsysweb.pojo.PageBean;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FrameService {

    @Autowired
    private FrameMapper frameDao;

    public void storeFrame(Frame frame) {
        frameDao.insertFrame(frame);
    }

    private List<FrameResponse> frameToFrameResponse(List<Frame> frames) {
        return frames.stream()
                .map(frame -> {
                    FrameResponse response = new FrameResponse();
                    response.setCheckCode(frame.getCheckCode());
                    response.setDeviceAddress(frame.getDeviceAddress());
                    response.setDirection(frame.getDirection());
                    response.setFrameNumber(frame.getFrameNumber());
                    response.setLength(frame.getLength());
                    response.setPortName(frame.getPortName());
                    response.setTime(frame.getTime());
                    response.setType(frame.getType());
                    response.setDataBase64("");
                    if (frame.getData() != null) {
                        response.setDataBase64(Base64.getEncoder().encodeToString(frame.getData()));
                    }
                    return response;
                }).collect(Collectors.toList());
    }

    public List<FrameResponse> loadFrames(String portName) {
        return frameToFrameResponse(frameDao.queryFrames(portName));
    }

    public PageBean listByPage(Integer page, Integer pageSize, String portName) {
        Long count = frameDao.countFrames(portName);

        int start = (page - 1) * pageSize;
        List<Frame> frames = frameDao.queryFramesByPage(portName, start, pageSize);

        return new PageBean(count, frameToFrameResponse(frames));
    }
}
