package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import pojo.DeviceStatus;
import pojo.Frame;
import service.DeviceStatusService;
import service.FrameService;
import utils.HexUtils;
import utils.LockStatusUtils;
import utils.SerialPortUtils;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static utils.SendUtils.*;

public class ParcelLockerCtrlSysFrame extends JFrame {
    // 常量 //
    private static final int QUERY_INTERVAL = 5000;
    private static final String DIRECTION_SEND_TEXT = "发送";
    private static final String DIRECTION_RECV_TEXT = "接收";
    private static final String COMPRESSOR_OFF_TEXT = "压缩机: 停止";
    private static final String COMPRESSOR_PRE_TEXT = "压缩机: 预启动";
    private static final String COMPRESSOR_ON_TEXT = "压缩机: 运行";
    private static final String COMPRESSOR_BROKEN_TEXT = "压缩机: 故障";
    private static final String AUTO_TEMP_OFF = "自动控温: 关闭";
    private static final String AUTO_TEMP_ON = "自动控温: 开启";

    // 组件 //
    private JPanel statusIndicator;
    private JLabel statusLabel;
    private JLabel deviceInfoLabel;
    private JLabel timeLabel;
    private Timer timeUpdateTimer;

    private DefaultTableModel tableModel;

    private JLabel deviceAddressLabel;
    private JLabel deviceCodeLabel;
    private JComboBox<String> deviceComboBox;

    private JButton autoTempButton;
    private JButton compressorStartButton;
    private JButton compressorStopButton;

    private JLabel compressorStatusLabel;
    private JLabel ackInfoLabel;

    private JLabel tempSettingLabel;
    private JLabel tempRealLabel;
    private JLabel tempDeviationLabel;

    private XYSeries setSeries;
    private XYSeries currentSeries;

    private final JLabel[] drawerLabels = new JLabel[10];

    // 设备管理 //
    private List<String> devicesPortName = new ArrayList<>();
    private final Map<String, Boolean> autoTempControl = new ConcurrentHashMap<>();
    private final Map<String, Boolean> online = new ConcurrentHashMap<>();
    private String currentDevicePortName;
    private DeviceStatus currentDeviceStatus;


    // 构造函数初始化 //
    public ParcelLockerCtrlSysFrame() {
        initDevices();
        initDeviceStatus();
        initUI();

        setTitle("智能快递柜终端");
        setSize(1000, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }


    // 初始化类函数 //
    private void initDevices() {
        devicesPortName = SerialPortUtils.getAvailablePortNames();
        if (devicesPortName.isEmpty()) {
            currentDevicePortName = "";
        } else {
            currentDevicePortName = devicesPortName.get(0);
            for (String portName : devicesPortName) {
                autoTempControl.put(portName, false);
                online.put(portName, false);
                Timer timer = new Timer(QUERY_INTERVAL,
                        e -> sendQueryCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                                portName, DeviceStatusService.loadLatestDeviceStatus(portName).getDeviceAddress())
                );
                timer.start();
            }
        }
    }

    private void initDeviceStatus() {
        if (!currentDevicePortName.isEmpty())
            currentDeviceStatus = DeviceStatusService.loadLatestDeviceStatus(currentDevicePortName);
        else
            currentDeviceStatus = new DeviceStatus();
    }

    private void initUI() {
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));

        // 1. 顶部状态栏
        mainPanel.add(createStatusBar(), BorderLayout.NORTH);

        // 2. 中部内容区
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        JPanel tableAndChartPanel = new JPanel();
        tableAndChartPanel.setLayout(new BoxLayout(tableAndChartPanel, BoxLayout.Y_AXIS));

        // 数据包表格
        JPanel tablePanel = createTablePanel();
        tablePanel.setPreferredSize(new Dimension(0, 200));
        updateFramesUI();
        tableAndChartPanel.add(tablePanel);

        // 温度曲线
        JPanel tempChartPanel = createTemperatureChartPanel();
        tempChartPanel.setPreferredSize(new Dimension(0, 150));
        tableAndChartPanel.add(tempChartPanel);
        updateTemperatureChart();

        centerPanel.add(tableAndChartPanel, BorderLayout.CENTER);
        centerPanel.add(createControlPanel(), BorderLayout.EAST);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. 底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(createTemperaturePanel(), BorderLayout.CENTER);
        bottomPanel.add(createDrawerPanel(), BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }


    // 组件类函数 //
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setBackground(new Color(50, 50, 50));

        // 状态指示器
        statusIndicator = new JPanel();
        statusIndicator.setPreferredSize(new Dimension(15, 15));
        statusIndicator.setBackground(Color.RED);
        statusIndicator.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        statusLabel = new JLabel(" 状态: 离线");
        statusLabel.setForeground(Color.WHITE);

        // 设备信息
        deviceInfoLabel = new JLabel(" 设备端口: " + currentDevicePortName);
        deviceInfoLabel.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(new Color(50, 50, 50));
        leftPanel.add(statusIndicator);
        leftPanel.add(statusLabel);
        leftPanel.add(deviceInfoLabel);

        // 时间显示
        timeLabel = new JLabel();
        timeLabel.setForeground(Color.WHITE);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(timeLabel, BorderLayout.EAST);

        // 启动时间更新定时器
        if (timeUpdateTimer != null)
            timeUpdateTimer.stop();
        timeUpdateTimer = new Timer(1000, e ->
                timeLabel.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date())));
        timeUpdateTimer.start();

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("数据包显示"));
        panel.setBackground(Color.WHITE);

        String[] columns = {"方向", "帧长", "帧号", "地址", "功能号", "数据", "校验码"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable framesTable = new JTable(tableModel);
        framesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        framesTable.setRowHeight(25);
        framesTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 表头样式
        framesTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        framesTable.getTableHeader().setBackground(new Color(70, 130, 180));
        framesTable.getTableHeader().setForeground(Color.BLACK);

        // 设置列宽分配
        int tableWidth = 600; // 表格总宽度
        int[] columnWidths = {
                (int) (tableWidth * 0.1),  // 方向 15%
                (int) (tableWidth * 0.05),  // 帧长 5%
                (int) (tableWidth * 0.05),  // 帧号 5%
                (int) (tableWidth * 0.05),  // 地址 5%
                (int) (tableWidth * 0.15), // 功能号 15%
                (int) (tableWidth * 0.45), // 数据 45%
                (int) (tableWidth * 0.1)   // 帧编码 10%
        };

        // 应用列宽设置
        for (int i = 0; i < columnWidths.length; i++) {
            framesTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(framesTable);
        scrollPane.setPreferredSize(new Dimension(tableWidth, 200));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCompressorStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("压缩机状态"));
        panel.setBackground(Color.WHITE);

        compressorStatusLabel = new JLabel("",
                JLabel.CENTER);
        compressorStatusLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        updateCompressorStatusLabel();

        panel.add(compressorStatusLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("控制面板"));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(300, 0));

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        Dimension buttonSize = new Dimension(250, 40);

        // 设备信息显示
        gbc.gridwidth = 2; // 两列
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("设备信息"));
        deviceAddressLabel = new JLabel("设备地址: " + currentDeviceStatus.getDeviceAddress());
        deviceCodeLabel = new JLabel("设备编码: 0x" + currentDeviceStatus.getDeviceCode());

        GridBagConstraints infoPanelGBC = new GridBagConstraints();
        infoPanelGBC.gridx = 0;
        infoPanelGBC.gridy = 0;
        infoPanelGBC.weightx = 0.4;
        infoPanelGBC.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(deviceAddressLabel, infoPanelGBC);
        infoPanelGBC.gridx = 1;
        infoPanelGBC.weightx = 0.6;
        infoPanel.add(deviceCodeLabel, infoPanelGBC);

        buttonPanel.add(infoPanel, gbc);

        // 设备选择下拉框
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JLabel deviceLabel = new JLabel("选择设备:");
        buttonPanel.add(deviceLabel, gbc);

        gbc.gridx = 1;
        deviceComboBox = new JComboBox<>(devicesPortName.toArray(String[]::new));
        deviceComboBox.setPreferredSize(buttonSize);
        deviceComboBox.setMaximumSize(buttonSize);
        deviceComboBox.addActionListener(e -> switchDevice((String) deviceComboBox.getSelectedItem()));
        buttonPanel.add(deviceComboBox, gbc);

        // 修改地址按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton addressButton = new JButton("修改地址");
        addressButton.setPreferredSize(buttonSize);
        addressButton.setMaximumSize(buttonSize);
        styleButton(addressButton, new Color(70, 130, 180));
        addressButton.addActionListener(e -> showAddressDialog());
        buttonPanel.add(addressButton, gbc);

        // 压缩机状态面板
        gbc.gridy = 3;
        buttonPanel.add(createCompressorStatusPanel(), gbc);

        // 压缩机控制按钮
        gbc.gridwidth = 1;
        gbc.gridy = 4;
        gbc.gridx = 0;
        compressorStartButton = new JButton("开启压缩机");
        styleButton(compressorStartButton, Color.CYAN);
        compressorStartButton.setPreferredSize(new Dimension(120, 40));
        compressorStartButton.setMaximumSize(new Dimension(120, 40));
        compressorStartButton.addActionListener(e ->
                sendCompressorStartControlCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                        currentDeviceStatus.getDeviceAddress(), currentDevicePortName));
        buttonPanel.add(compressorStartButton, gbc);
        gbc.gridx = 1;
        compressorStopButton = new JButton("关闭压缩机");
        styleButton(compressorStopButton, Color.CYAN);
        compressorStopButton.setPreferredSize(new Dimension(120, 40));
        compressorStopButton.setMaximumSize(new Dimension(120, 40));
        compressorStopButton.addActionListener(e ->
                sendCompressorStopControlCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                        currentDeviceStatus.getDeviceAddress(), currentDevicePortName));
        buttonPanel.add(compressorStopButton, gbc);

        // 自动控温按钮
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 5;
        autoTempButton = new JButton(autoTempControl.get(currentDevicePortName) ?
                AUTO_TEMP_ON : AUTO_TEMP_OFF);
        autoTempButton.setPreferredSize(buttonSize);
        autoTempButton.setMaximumSize(buttonSize);
        styleButton(autoTempButton, autoTempControl.get(currentDevicePortName)
                ? new Color(34, 139, 34) : Color.RED);
        autoTempButton.addActionListener(e -> toggleAutoTemperature());
        buttonPanel.add(autoTempButton, gbc);

        // 打开抽屉按钮
        gbc.gridy = 6;
        JButton openDrawerButton = new JButton("打开抽屉");
        openDrawerButton.setPreferredSize(buttonSize);
        openDrawerButton.setMaximumSize(buttonSize);
        styleButton(openDrawerButton, new Color(70, 130, 180));
        openDrawerButton.addActionListener(e -> showDrawerSelectionDialog());
        buttonPanel.add(openDrawerButton, gbc);

        // 设备参数按钮
        gbc.gridy = 7;
        JButton settingsButton = new JButton("设备参数设置");
        settingsButton.setPreferredSize(buttonSize);
        settingsButton.setMaximumSize(buttonSize);
        styleButton(settingsButton, new Color(70, 130, 180));
        settingsButton.addActionListener(e -> showSettingsDialog());
        buttonPanel.add(settingsButton, gbc);

        gbc.gridy = 8;
        gbc.weighty = 1.0;
        buttonPanel.add(Box.createVerticalGlue(), gbc);
        panel.add(buttonPanel, BorderLayout.CENTER);

        ackInfoLabel = new JLabel("");
        ackInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(ackInfoLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTemperatureChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("温度曲线"));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 150));

        List<Double> historySetTemperature = DeviceStatusService
                .loadHistorySetTemperature(currentDevicePortName);
        List<Double> historyCurrentTemperature = DeviceStatusService
                .loadHistoryCurrentTemperature(currentDevicePortName);
        // 创建数据集
        XYSeriesCollection dataset = new XYSeriesCollection();
        setSeries = new XYSeries("设定温度");
        currentSeries = new XYSeries("当前温度");

        int xSize = historySetTemperature.size();
        for (int i = 0; i < xSize; i++) {
            setSeries.add(i, historySetTemperature.get(i));
            currentSeries.add(i, historyCurrentTemperature.get(i));
        }

        dataset.addSeries(setSeries);
        dataset.addSeries(currentSeries);

        // 创建图表
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                null,
                "时间",
                "温度(℃)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // 正确设置抗锯齿
        xylineChart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        xylineChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font font = new Font("微软雅黑", Font.PLAIN, 10);
        // 设置图例字体
        xylineChart.getLegend().setItemFont(font);
        xylineChart.getLegend().setBackgroundPaint(new Color(240, 240, 240));

        // 设置x,y轴字体和十字准线
        XYPlot plot = xylineChart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        // 设置十字准线
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);
        plot.setDomainCrosshairPaint(Color.GRAY);
        plot.setRangeCrosshairPaint(Color.GRAY);
        plot.setDomainCrosshairStroke(new BasicStroke(1.0f));
        plot.setRangeCrosshairStroke(new BasicStroke(1.0f));

        plot.getDomainAxis().setLabelFont(font);
        plot.getDomainAxis().setTickLabelFont(font);
        plot.getRangeAxis().setLabelFont(font);
        plot.getRangeAxis().setTickLabelFont(font);

        // 自定义渲染器以设置线条颜色
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        // 设置"设定温度"的颜色为红色虚线
        renderer.setSeriesPaint(0, new Color(200, 50, 50));
        renderer.setSeriesStroke(0, new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f));
        // 设置"当前温度"的颜色为蓝色实线
        renderer.setSeriesPaint(1, new Color(50, 50, 200));
        renderer.setSeriesStroke(1, new BasicStroke(2.5f));

        renderer.setSeriesShapesFilled(0, false);
        renderer.setSeriesShapesFilled(1, false);
        renderer.setSeriesShape(0, null);
        renderer.setSeriesShape(1, null);
        plot.setRenderer(renderer);

        // 将图表添加到面板上
        ChartPanel chartPanel = new ChartPanel(xylineChart) {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                Point2D p = this.translateScreenToJava2D(e.getPoint());
                XYPlot plot = (XYPlot) this.getChart().getPlot();
                plot.setDomainCrosshairValue(plot.getDomainAxis().java2DToValue(p.getX(),
                        this.getScreenDataArea(), RectangleEdge.BOTTOM));
                plot.setRangeCrosshairValue(plot.getRangeAxis().java2DToValue(p.getY(),
                        this.getScreenDataArea(), RectangleEdge.LEFT));
                plot.setDomainCrosshairVisible(true);
                plot.setRangeCrosshairVisible(true);
            }
        };
        chartPanel.setPreferredSize(new Dimension(800, 600));
        chartPanel.setMouseZoomable(true, false);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(false);

        panel.add(chartPanel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();

        return panel;
    }

    private JPanel createTemperaturePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBorder(BorderFactory.createTitledBorder("温度监控"));
        panel.setBackground(Color.WHITE);

        // 设定温度
        JPanel setTempPanel = new JPanel(new BorderLayout());
        setTempPanel.setBorder(BorderFactory.createTitledBorder("设定温度"));
        setTempPanel.setBackground(Color.WHITE);
        tempSettingLabel = new JLabel(currentDeviceStatus.getSetTemperature() + "℃", JLabel.CENTER);
        tempSettingLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        tempSettingLabel.setForeground(new Color(70, 130, 180));
        setTempPanel.add(tempSettingLabel, BorderLayout.CENTER);
        panel.add(setTempPanel);

        // 采集温度
        JPanel realTempPanel = new JPanel(new BorderLayout());
        realTempPanel.setBorder(BorderFactory.createTitledBorder("采集温度"));
        realTempPanel.setBackground(Color.WHITE);
        tempRealLabel = new JLabel(currentDeviceStatus.getCurrentTemperature() + "℃", JLabel.CENTER);
        tempRealLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        tempRealLabel.setForeground(new Color(70, 130, 180));
        realTempPanel.add(tempRealLabel, BorderLayout.CENTER);
        panel.add(realTempPanel);

        // 控制温度偏差
        JPanel deviationPanel = new JPanel(new BorderLayout());
        deviationPanel.setBorder(BorderFactory.createTitledBorder("控制温度偏差"));
        deviationPanel.setBackground(Color.WHITE);
        tempDeviationLabel = new JLabel(currentDeviceStatus.getTemperatureDeviation() + "℃", JLabel.CENTER);
        tempDeviationLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        tempDeviationLabel.setForeground(new Color(70, 130, 180));
        deviationPanel.add(tempDeviationLabel, BorderLayout.CENTER);
        panel.add(deviationPanel);

        // 温度设置按钮
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        controlPanel.setBackground(Color.WHITE);

        JButton tempSetButton = new JButton("设置温度");
        styleButton(tempSetButton, new Color(70, 130, 180));
        tempSetButton.addActionListener(e -> showSetTemperatureDialog());
        controlPanel.add(tempSetButton, BorderLayout.WEST);

        // 温度偏差设置按钮
        JButton tempDeviationSetButton = new JButton("设置温度偏差");
        styleButton(tempDeviationSetButton, new Color(70, 130, 180));
        tempDeviationSetButton.addActionListener(e -> showSetTemperatureDeviationDialog());
        controlPanel.add(tempDeviationSetButton, BorderLayout.EAST);

        panel.add(controlPanel);

        return panel;
    }

    private JPanel createDrawerPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("抽屉状态"));
        panel.setBackground(Color.WHITE);

        // 上排抽屉1-5
        JPanel topPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        topPanel.setBackground(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            drawerLabels[i] = createDrawerLabel(i);
            topPanel.add(drawerLabels[i]);
        }
        panel.add(topPanel);

        // 下排抽屉6-10
        JPanel bottomPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        bottomPanel.setBackground(Color.WHITE);
        for (int i = 5; i < 10; i++) {
            drawerLabels[i] = createDrawerLabel(i);
            bottomPanel.add(drawerLabels[i]);
        }
        panel.add(bottomPanel);

        return panel;
    }

    private JLabel createDrawerLabel(int drawerNumber) {
        JLabel label;
        if (!LockStatusUtils.isLocked(currentDeviceStatus.getLockStatus(), drawerNumber)) {
            label = new JLabel("抽屉" + (drawerNumber + 1) + " (开)", JLabel.CENTER);
            label.setBackground(new Color(34, 139, 34));
        } else {
            label = new JLabel("抽屉" + (drawerNumber + 1) + " (关)", JLabel.CENTER);
            label.setBackground(Color.RED);
        }
        label.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        label.setOpaque(true);

        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return label;
    }

    private void toggleAutoTemperature() {
        autoTempControl.put(currentDevicePortName, !autoTempControl.get(currentDevicePortName));

        if (autoTempControl.get(currentDevicePortName)) {
            autoTempButton.setText(AUTO_TEMP_ON);
            autoTempButton.setBackground(new Color(34, 139, 34));
            compressorStartButton.setEnabled(false);
            compressorStopButton.setEnabled(false);
        } else {
            autoTempButton.setText(AUTO_TEMP_OFF);
            autoTempButton.setBackground(Color.RED);
            compressorStartButton.setEnabled(true);
            compressorStopButton.setEnabled(true);
        }
    }

    private void showDrawerSelectionDialog() {
        JDialog dialog = new JDialog(this, "抽屉控制 - " + currentDevicePortName, true);
        dialog.setSize(450, 350);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        final List<Integer> indexes = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < DeviceStatus.DRAWER_NUM; i++) {
            JToggleButton drawerBtn = new JToggleButton("");
            drawerBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            drawerBtn.setFocusPainted(false);
            drawerBtn.setContentAreaFilled(false);
            drawerBtn.setOpaque(true);

            if (LockStatusUtils.isLocked(currentDeviceStatus.getLockStatus(), i)) {
                drawerBtn.setText("抽屉" + (i + 1) + " (关闭)");
                drawerBtn.setBackground(Color.RED);
                drawerBtn.setSelected(false);
            } else {
                drawerBtn.setText("抽屉" + (i + 1) + " (开启)");
                drawerBtn.setBackground(new Color(34, 139, 34));
                drawerBtn.setSelected(true);
                indexes.add(i);
            }

            drawerBtn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    if (drawerBtn.isSelected()) {
                        drawerBtn.setBackground(new Color(50, 205, 50));
                    } else {
                        drawerBtn.setBackground(new Color(255, 100, 100));
                    }
                }

                public void mouseExited(MouseEvent evt) {
                    if (drawerBtn.isSelected()) {
                        drawerBtn.setBackground(new Color(34, 139, 34));
                    } else {
                        drawerBtn.setBackground(Color.RED);
                    }
                }
            });

            int drawerIndex = i;
            drawerBtn.addActionListener(e -> {
                if (drawerBtn.isSelected()) {
                    indexes.add(drawerIndex);
                    drawerBtn.setText("抽屉" + (drawerIndex + 1) + " (开锁)");
                    drawerBtn.setBackground(new Color(34, 139, 34));
                } else {
                    indexes.removeIf(x -> x == drawerIndex);
                    drawerBtn.setText("抽屉" + (drawerIndex + 1) + " (关闭)");
                    drawerBtn.setBackground(Color.RED);
                }
            });

            mainPanel.add(drawerBtn);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton openAllBtn = new JButton("全部开锁");
        styleButton(openAllBtn, new Color(70, 130, 180));
        openAllBtn.addActionListener(e -> {
            indexes.clear();
            for (int i = 0; i < DeviceStatus.DRAWER_NUM; i++) {
                indexes.add(i);
            }
            sendUnlockCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                    indexes);
            dialog.dispose();
        });

        JButton closeAllBtn = new JButton("全部关闭");
        styleButton(closeAllBtn, Color.RED);
        closeAllBtn.addActionListener(e -> {
            indexes.clear();
            sendUnlockCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                    indexes);
            dialog.dispose();
        });

        JButton confirmBtn = new JButton("确认");
        styleButton(confirmBtn, new Color(70, 130, 180));
        confirmBtn.addActionListener(e -> {
            sendUnlockCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                    indexes);
            dialog.dispose();
        });

        buttonPanel.add(openAllBtn);
        buttonPanel.add(closeAllBtn);
        buttonPanel.add(confirmBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showSettingsDialog() {
        JDialog dialog = new JDialog(this, "设备参数设置 - " + currentDevicePortName, true);
        dialog.setSize(450, 350);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("设备端口:"));
        JTextField portField = new JTextField(currentDevicePortName);
        portField.setEditable(false);
        panel.add(portField);

        panel.add(new JLabel("设备编码(十位十六进制):"));
        JTextField deviceCodeField = new JTextField(currentDeviceStatus.getDeviceCode());
        panel.add(deviceCodeField);

        panel.add(new JLabel("设备地址(1-120):"));
        JTextField deviceAddressField = new JTextField(Integer.toString(currentDeviceStatus.getDeviceAddress()));
        panel.add(deviceAddressField);

        panel.add(new JLabel("上传间隔(s):"));
        JTextField intervalField = new JTextField(Integer.toString(currentDeviceStatus.getStatusUploadInterval()));
        panel.add(intervalField);

        panel.add(new JLabel("压缩机延时(s):"));
        JTextField delayField = new JTextField(Integer.toString(currentDeviceStatus.getCompressorStartupDelay()));
        panel.add(delayField);

        panel.add(new JLabel("设定温度(℃):"));
        JSpinner tempSpinner = new JSpinner(new SpinnerNumberModel(
                currentDeviceStatus.getSetTemperature(), DeviceStatus.MIN_TEMP, DeviceStatus.MAX_TEMP, 0.5));
        panel.add(tempSpinner);

        panel.add(new JLabel("温度偏差(℃):"));
        JSpinner deviationSpinner = new JSpinner(new SpinnerNumberModel(
                currentDeviceStatus.getTemperatureDeviation(), 0, 50, 1));
        panel.add(deviationSpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("保存");
        styleButton(saveButton, new Color(70, 130, 180));
        saveButton.addActionListener(e -> {
            String deviceCode = deviceCodeField.getText();
            int deviceAddress = Integer.parseInt(deviceAddressField.getText());
            double temperature = (double) tempSpinner.getValue();
            int deviation = (int) deviationSpinner.getValue();
            int interval = Integer.parseInt(intervalField.getText());
            int delay = Integer.parseInt(delayField.getText());

            sendSetParamCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                    deviceCode,
                    deviceAddress,
                    temperature,
                    delay,
                    interval,
                    deviation
            );

            dialog.dispose();
        });

        JButton cancelButton = new JButton("取消");
        styleButton(cancelButton, Color.GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showSetTemperatureDialog() {
        JDialog dialog = new JDialog(this, "设置温度设置 - " + currentDevicePortName, true);
        dialog.setSize(300, 140);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setPreferredSize(new Dimension(250, 40));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JSpinner tempSpinner = new JSpinner(new
                SpinnerNumberModel(
                currentDeviceStatus.getSetTemperature(), DeviceStatus.MIN_TEMP, DeviceStatus.MAX_TEMP, 0.5));
        panel.add(tempSpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("保存");
        styleButton(saveButton, new Color(70, 130, 180));
        saveButton.addActionListener(e -> {
            double temperature = (double) tempSpinner.getValue();

            if (temperature >= DeviceStatus.MIN_TEMP && temperature <= DeviceStatus.MAX_TEMP) {
                sendSetTemperatureCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                        temperature);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "温度必须在" + DeviceStatus.MIN_TEMP + "℃到" + DeviceStatus.MAX_TEMP + "℃之间",
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            dialog.dispose();
        });

        JButton cancelButton = new JButton("取消");
        styleButton(cancelButton, Color.GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showSetTemperatureDeviationDialog() {
        JDialog dialog = new JDialog(this, "温度偏差设置 - " + currentDevicePortName, true);
        dialog.setSize(300, 140);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setPreferredSize(new Dimension(250, 40));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JSpinner tempSpinner = new JSpinner(new
                SpinnerNumberModel(
                currentDeviceStatus.getTemperatureDeviation(), 0, 50, 1));
        panel.add(tempSpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("保存");
        styleButton(saveButton, new Color(70, 130, 180));
        saveButton.addActionListener(e -> {
            int temperatureDeviation = (int) tempSpinner.getValue();

            if (temperatureDeviation >= 0 && temperatureDeviation <= 50) {
                sendSetTemperatureDeviationCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                        temperatureDeviation);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "温度偏差必须在" + 0 + "℃到" + 50 + "℃之间",
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            dialog.dispose();
        });

        JButton cancelButton = new JButton("取消");
        styleButton(cancelButton, Color.GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddressDialog() {
        JDialog dialog = new JDialog(this, "修改设备地址 - " + currentDevicePortName, true);
        dialog.setSize(350, 200);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("设备端口:"));
        JTextField portField = new JTextField(currentDevicePortName);
        portField.setEditable(false);
        panel.add(portField);

        panel.add(new JLabel("新地址:"));
        JTextField newAddressField = new JTextField(Integer.toString(currentDeviceStatus.getDeviceAddress()));
        panel.add(newAddressField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("保存");
        styleButton(saveButton, new Color(70, 130, 180));
        saveButton.addActionListener(e -> {
            sendSetDeviceAddressCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                    currentDeviceStatus.getDeviceCode(), Integer.parseInt(newAddressField.getText()));
            dialog.dispose();
        });

        JButton cancelButton = new JButton("取消");
        styleButton(cancelButton, Color.GRAY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    // 更新类函数 //
    private void switchDevice(String portName) {
        currentDevicePortName = portName;
        currentDeviceStatus = DeviceStatusService.loadLatestDeviceStatus(portName);
        deviceInfoLabel.setText(" 设备端口: " + currentDevicePortName);
        deviceAddressLabel.setText("设备地址: " + currentDeviceStatus.getDeviceAddress());
        deviceCodeLabel.setText("设备地址: " + currentDeviceStatus.getDeviceAddress());
        setSeries.clear();
        currentSeries.clear();
        updateDeviceStateUI();
        updateFramesUI();
    }

    private void updateFramesUI() {
        tableModel.setRowCount(0);
        List<Frame> list = FrameService.loadFrames(currentDevicePortName);
        for (Frame frame : list) {
            tableModel.insertRow(0, new Object[]{
                    getDirectionText(frame.getDirection()),
                    frame.getLength(),
                    frame.getFrameNumber(),
                    frame.getDeviceAddress(),
                    getFuncTypeText(frame.getType()),
                    HexUtils.bytesToHex(frame.getData()),
                    String.format("%04X", frame.getCheckCode() & 0xFFFF)
            });
        }
    }

    private void updateDeviceStateUI() {
        // 更新温度显示
        tempSettingLabel.setText(String.format("%.1f℃", currentDeviceStatus.getSetTemperature()));
        tempRealLabel.setText(String.format("%.1f℃", currentDeviceStatus.getCurrentTemperature()));
        tempDeviationLabel.setText(currentDeviceStatus.getTemperatureDeviation() + "℃");

        // 更新压缩机状态
        updateCompressorStatusLabel();

        // 更新抽屉状态
        for (int i = 0; i < DeviceStatus.DRAWER_NUM; i++) {
            if (!LockStatusUtils.isLocked(currentDeviceStatus.getLockStatus(), i)) {
                drawerLabels[i].setText("抽屉" + (i + 1) + " (开)");
                drawerLabels[i].setBackground(new Color(34, 139, 34));
            } else {
                drawerLabels[i].setText("抽屉" + (i + 1) + " (关)");
                drawerLabels[i].setBackground(Color.RED);
            }
        }

        // 更新系统状态指示灯
        updateDeviceSystemStatus();

        // 更新地址显示
        deviceAddressLabel.setText("设备地址: " + currentDeviceStatus.getDeviceAddress());
        deviceCodeLabel.setText("设备编码: 0x" + currentDeviceStatus.getDeviceCode());

        // 更新自动控温显示
        if (autoTempControl.get(currentDevicePortName)) {
            autoTempButton.setText(AUTO_TEMP_ON);
            autoTempButton.setBackground(new Color(34, 139, 34));
            compressorStartButton.setEnabled(false);
            compressorStopButton.setEnabled(false);
        } else {
            autoTempButton.setText(AUTO_TEMP_OFF);
            autoTempButton.setBackground(Color.RED);
            compressorStartButton.setEnabled(true);
            compressorStopButton.setEnabled(true);
        }

        // 更新温度曲线图表
        updateTemperatureChart();
    }

    private void updateTemperatureChart() {
        List<Double> newHistorySetTemperature = DeviceStatusService
                .loadHistorySetTemperature(currentDevicePortName);
        List<Double> newHistoryCurrentTemperature = DeviceStatusService
                .loadHistoryCurrentTemperature(currentDevicePortName);

        int oldSize = setSeries.getItemCount();
        int newSize = newHistorySetTemperature.size();
        for (int i = oldSize; i < newSize; i++) {
            setSeries.add(i, newHistorySetTemperature.get(i));
            currentSeries.add(i, newHistoryCurrentTemperature.get(i));
        }
    }

    private void updateCompressorStatusLabel() {
        String status = getCompressorStatusText(currentDeviceStatus.getCompressorStatus());
        compressorStatusLabel.setText(status);

        switch (status) {
            case COMPRESSOR_PRE_TEXT -> compressorStatusLabel.setForeground(Color.ORANGE);
            case COMPRESSOR_ON_TEXT -> compressorStatusLabel.setForeground(new Color(34, 139, 34));
            case COMPRESSOR_OFF_TEXT -> compressorStatusLabel.setForeground(Color.RED);
            default -> compressorStatusLabel.setForeground(Color.YELLOW);
        }
    }

    private void updateDeviceSystemStatus() {
        /* 
        // 以上传数据帧中系统状态作为基准
        byte systemStatus = currentDeviceStatus.getSystemStatus();
        if (systemStatus == DeviceStatus.SYSTEM_STATUS_ON) {
            statusIndicator.setBackground(Color.GREEN);
            statusLabel.setText(" 状态：运行");
        } else if (systemStatus == DeviceStatus.SYSTEM_STATUS_PRE) {
            statusIndicator.setBackground(Color.YELLOW);
            statusLabel.setText(" 状态：预启动");
        } else if (systemStatus == DeviceStatus.SYSTEM_STATUS_OFF) {
            statusIndicator.setBackground(Color.RED);
            statusLabel.setText(" 状态：停机");
        } else {
            statusIndicator.setBackground(Color.BLACK);
            statusLabel.setText(" 状态：未知");
        }
        */

        // 以查询帧作为基准
        if (online.get(currentDevicePortName)) {
            statusIndicator.setBackground(Color.GREEN);
            statusLabel.setText(" 状态：在线");
        } else {
            statusIndicator.setBackground(Color.RED);
            statusLabel.setText(" 状态：离线");
        }
    }


    // 工具类函数 //
    private String getFuncTypeText(byte type) {
        return switch (type) {
            case Frame.QUERY_TYPE -> "Query";
            case Frame.COMPRESSOR_CONTROL_TYPE -> "CompressorControl";
            case Frame.UNLOCK_TYPE -> "Unlock";
            case Frame.SET_TEMPERATURE_TYPE -> "SetTemperature";
            case Frame.SET_PARAM_TYPE -> "SetParam";
            case Frame.SET_TEMPERATURE_DEVIATION_TYPE -> "SetTemperatureDeviation";
            case Frame.SET_DEVICE_ADDRESS_TYPE -> "SetDeviceAddress";
            case Frame.UPLOAD_STATUS_TYPE -> "UploadStatus";
            default -> "Unknown";
        };
    }

    private String getCompressorStatusText(byte compressorStatus) {
        return switch (compressorStatus) {
            case DeviceStatus.COMPRESSOR_STATUS_OFF -> COMPRESSOR_OFF_TEXT;
            case DeviceStatus.COMPRESSOR_STATUS_PRE -> COMPRESSOR_PRE_TEXT;
            case DeviceStatus.COMPRESSOR_STATUS_ON -> COMPRESSOR_ON_TEXT;
            case DeviceStatus.COMPRESSOR_STATUS_BROKEN -> COMPRESSOR_BROKEN_TEXT;
            default -> "Unknown";
        };
    }

    private String getDirectionText(int direction) {
        if (direction == Frame.DIRECTION_RECV) {
            return DIRECTION_RECV_TEXT;
        } else if (direction == Frame.DIRECTION_SEND) {
            return DIRECTION_SEND_TEXT;
        } else {
            return "Unknown";
        }
    }

    private void styleButton(AbstractButton button, Color bgColor) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }


    // 交互 - 通知类函数 //
    public void updateFrame(Frame newFrame) {
        if (!Objects.equals(currentDevicePortName, newFrame.getPortName())) return;
        SwingUtilities.invokeLater(() -> tableModel.insertRow(0, new Object[]{
                getDirectionText(newFrame.getDirection()),
                newFrame.getLength(),
                newFrame.getFrameNumber(),
                newFrame.getDeviceAddress(),
                getFuncTypeText(newFrame.getType()),
                HexUtils.bytesToHex(newFrame.getData()),
                String.format("%04X", newFrame.getCheckCode() & 0xFFFF)
        }));
    }

    public void updateDeviceState(DeviceStatus deviceStatus) {
        SwingUtilities.invokeLater(() -> {
            if (deviceStatus.getCompressorStatus() != DeviceStatus.COMPRESSOR_STATUS_BROKEN) {
                if (autoTempControl.get(deviceStatus.getPortName())) {
                    if (deviceStatus.getCurrentTemperature() >
                            deviceStatus.getSetTemperature() + deviceStatus.getTemperatureDeviation()
                            && deviceStatus.getCompressorStatus() == DeviceStatus.COMPRESSOR_STATUS_OFF) {
                        sendCompressorStartControlCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                                deviceStatus.getDeviceAddress(), deviceStatus.getPortName());
                    } else if (deviceStatus.getCurrentTemperature() <
                            deviceStatus.getSetTemperature() - deviceStatus.getTemperatureDeviation()
                            && deviceStatus.getCompressorStatus() == DeviceStatus.COMPRESSOR_STATUS_ON) {
                        sendCompressorStopControlCommand(currentDevicePortName, currentDeviceStatus.getDeviceAddress(),
                                deviceStatus.getDeviceAddress(), deviceStatus.getPortName());
                    }
                }
            }
            online.replace(deviceStatus.getPortName(), false, true);

            if (!Objects.equals(currentDevicePortName, deviceStatus.getPortName())) return;

            currentDeviceStatus = deviceStatus;
            updateDeviceStateUI();
        });
    }

    public void notifyQueryError(String portName) {
        SwingUtilities.invokeLater(() -> {
            online.replace(portName, true, false);
            if (portName.equals(currentDevicePortName)) {
                updateDeviceSystemStatus();
            }
        });
    }

    public void notifyACKError(int frameNumber, String portName) {
        SwingUtilities.invokeLater(() -> {
            JWindow popup = new JWindow();
            popup.setAlwaysOnTop(true);

            JLabel label = new JLabel(portName + "未接收到ACK帧: 帧号" + frameNumber);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setForeground(Color.WHITE);
            label.setBackground(new Color(200, 50, 50, 200));
            label.setOpaque(true);
            popup.add(label);
            popup.pack();

            // 顶部
            Point loc = this.getLocation();
            Dimension size = this.getSize();
            popup.setLocation(
                    loc.x + (size.width - popup.getWidth()) / 2,
                    loc.y + 35
            );

            popup.setVisible(true);

            // 淡出动画
            Timer fadeTimer = new Timer(50, null);
            fadeTimer.addActionListener(e -> {
                float opacity = popup.getOpacity();
                if (opacity > 0.1f) {
                    popup.setOpacity(opacity - 0.1f);
                } else {
                    popup.dispose();
                    fadeTimer.stop();
                }
            });

            new Timer(1500, e -> fadeTimer.start()).start();
        });
    }

    public void setACKWaiting() {
        SwingUtilities.invokeLater(() -> ackInfoLabel.setText("等待ACK帧中..."));
    }

    public void unsetACKWaiting() {
        SwingUtilities.invokeLater(() -> ackInfoLabel.setText(""));
    }
}