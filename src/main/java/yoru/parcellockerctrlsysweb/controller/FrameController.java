package yoru.parcellockerctrlsysweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import yoru.parcellockerctrlsysweb.pojo.FrameResponse;
import yoru.parcellockerctrlsysweb.pojo.PageBean;
import yoru.parcellockerctrlsysweb.pojo.Result;
import yoru.parcellockerctrlsysweb.service.FrameService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/frame")
public class FrameController {
    @Autowired
    private FrameService frameService;

    @GetMapping("/page/{portName}")
    public Result listByPage(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "3") Integer pageSize,
                             @PathVariable String portName) {
        log.info("Query All Frame Data By Page {} (PageSize={}).", page, pageSize);

        PageBean pageBean = frameService.listByPage(page, pageSize, portName);

        return Result.success(pageBean);
    }

    @GetMapping("/{portName}")
    public Result listAll(@PathVariable String portName) {
        log.info("Query All Frame Data.");

        List<FrameResponse> frames = frameService.loadFrames(portName);

        return Result.success(frames);
    }

}
