package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.LocalDateTime;
import jdbc.Config;
import jdbc.DatabaseManager;
import models.Bot.BotManager;
import models.event.EventManager;
import models.item.Item;
import models.player.Inventory;
import models.player.Player;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.Service;
import services.TaskService;
import utils.Logger;

public class MenuAuto extends JFrame {
    public static boolean isRunning = false;
    private static byte khuyenMaiNap = 1;
    private JProgressBar cpuBar, ramBar;
    private JLabel lblCpuValue, lblRamValue;
    private JLabel lblThreads, lblOnline, lblBots, lblLichBaoTri;
    private JTextArea txtViewer;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final OperatingSystemMXBean osBean =
        (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private final DecimalFormat df = new DecimalFormat("0.0");

    public MenuAuto() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle("HỆ ĐIỀU HÀNH ZINZIN");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null); // hiển thị giữa màn hình
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirm("Bạn có chắc muốn thoát?")) {
                    System.exit(0);
                }
            }
        });
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);
        JPanel panelButtons = new JPanel(new GridLayout(3, 3, 5, 5));
        panelButtons.setPreferredSize(new Dimension(600, 300));
        String[] btnNames = {
            "Bảo Trì Ngay", "Kick Toàn Bộ Player", "Đổi Tốc Độ EXP",
            "Nạp VNĐ", "Next Nhiệm Vụ", "Buff Item",
            "Buff Item SKH", "Đổi Sự Kiện", "Chống DDoS"
        };
        for (String name : btnNames) {
            JButton btn = new JButton(name);
            btn.setFont(new Font("Consolas", Font.PLAIN, 14));
            btn.setPreferredSize(new Dimension(180, 90));
            btn.addActionListener(e -> handleAction(name));
            panelButtons.add(btn);
        }
        txtViewer = new JTextArea();
        txtViewer.setEditable(false);
        txtViewer.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane spViewer = new JScrollPane(txtViewer);
        spViewer.setPreferredSize(new Dimension(600, 180));
        JPanel center = new JPanel(new BorderLayout(0, 5));
        center.add(panelButtons, BorderLayout.NORTH);
        center.add(spViewer, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        JPanel panelStatus = new JPanel();
        panelStatus.setLayout(new BoxLayout(panelStatus, BoxLayout.Y_AXIS));
        panelStatus.setBorder(BorderFactory.createTitledBorder("Trạng Thái Hệ Thống"));
        panelStatus.setPreferredSize(new Dimension(200, 0));
        lblThreads = createLabel("Threads: 0");
        lblOnline  = createLabel("Player Online: 0");
        lblBots    = createLabel("Bot Online: 0");
        lblLichBaoTri = createLabel("");
        cpuBar      = createProgressBar("                       CPU");
        lblCpuValue = createLabel("0.0 %");
        ramBar      = createProgressBar("                       RAM");
        lblRamValue = createLabel("0 MB / 0 MB");
        panelStatus.add(lblThreads);
        panelStatus.add(lblOnline);
        panelStatus.add(lblBots);
        panelStatus.add(Box.createVerticalStrut(10));
        panelStatus.add(cpuBar);
        panelStatus.add(lblCpuValue);
        panelStatus.add(ramBar);
        panelStatus.add(lblRamValue);
        panelStatus.add(Box.createVerticalGlue());
        panelStatus.add(lblLichBaoTri);
        root.add(panelStatus, BorderLayout.EAST);
        JPanel panelSchedule = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelSchedule.setPreferredSize(new Dimension(0, 40));
        JComboBox<Integer> cbHour = new JComboBox<>(model(0, 23));
        JComboBox<Integer> cbMin  = new JComboBox<>(model(0, 59));
        cbHour.setPreferredSize(new Dimension(50, 25));
        cbMin .setPreferredSize(new Dimension(50, 25));
        JButton btnSchedule = new JButton("Lên Lịch Bảo Trì");
        btnSchedule.setPreferredSize(new Dimension(120, 25));
        btnSchedule.addActionListener(e -> {
            lichHenBaoTri(cbHour, cbMin);
            loadServerLog();
        });
        panelSchedule.add(new JLabel("Giờ:"));
        panelSchedule.add(cbHour);
        panelSchedule.add(new JLabel("Phút:"));
        panelSchedule.add(cbMin);
        panelSchedule.add(btnSchedule);
        root.add(panelSchedule, BorderLayout.SOUTH);
        napCauHinhBaoTri(cbHour, cbMin);
        loadServerLog();
        Timer logTimer = new Timer(1000, e -> loadServerLog());
        logTimer.start();
        pack();
        setVisible(true);
        ServerManager.gI().run();
        startMonitoring();
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Consolas", Font.PLAIN, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JProgressBar createProgressBar(String title) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(false);
        bar.setBorder(BorderFactory.createTitledBorder(title));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        return bar;
    }
    private DefaultComboBoxModel<Integer> model(int min, int max) {
        DefaultComboBoxModel<Integer> m = new DefaultComboBoxModel<>();
        for (int i = min; i <= max; i++) {
            m.addElement(i);
        }
        return m;
    }

    private void startMonitoring() {
        scheduler.scheduleAtFixedRate(() -> SwingUtilities.invokeLater(() -> {
            lblThreads.setText("Threads: " + Thread.activeCount());
            lblOnline.setText("Player Online: " + Client.gI().getPlayers().size());
            lblBots.setText("Bot Online: " + BotManager.gI().bot.size());
            // CPU
            double cpu = osBean.getSystemCpuLoad() * 100;
            cpuBar.setValue((int) cpu);
            lblCpuValue.setText(df.format(cpu) + " %");
            // RAM 
            long total = osBean.getTotalPhysicalMemorySize();
            long free  = osBean.getFreePhysicalMemorySize();
            int pct = (int) ((total - free) * 100 / total);
            ramBar.setValue(pct);
            // Hiển thị dưới dạng "xx.x %"
            lblRamValue.setText(df.format(pct) + " %");
        }), 0, 1, TimeUnit.SECONDS);
    }

    private void loadServerLog() {
        SwingUtilities.invokeLater(() -> {
            File f = new File("logs", "server.log");
            if (!f.exists()) {
                txtViewer.setText("Không tìm thấy file log: " + f.getAbsolutePath());
                return;
            }
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                txtViewer.read(br, null);
            } catch (IOException e) {
                txtViewer.setText("Lỗi đọc file log: " + f.getAbsolutePath());
            }
        });
    }

    private void handleAction(String name) {
        switch (name) {
            case "Bảo Trì Ngay":
                if (confirm("Bạn có chắc muốn bảo trì ngay?")) {
                    Maintenance.gI().start(5);  // Bảo trì 5 phút
                }
                break;
            case "Kick Toàn Bộ Player":
                new Thread(() -> Client.gI().close()).start();
                break;
            case "Đổi Tốc Độ EXP": thayDoiExp(); break;
            case "Nạp VNĐ": hienThiFormNapVND(); break;
            case "Next Nhiệm Vụ": capNhatNextNhiemVu(); break;
            case "Buff Item": hienThiFormBuffItem(); break;
            case "Buff Item SKH": hienThiFormBuffItemSKH(); break;
            case "Đổi Sự Kiện": hienThiFormDoiSuKien(); break;
            case "Chống DDoS": batChongDDoS(); break;
        }
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Xác nhận",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void hienThiDialogBaoTri() {
    
    }

    private void kickTatCa() {
        new Thread(() -> Client.gI().close()).start();
    }

    private void thayDoiExp() {
        String in = JOptionPane.showInputDialog(this,
            "Exp hiện tại: " + Config.RATE_EXP_SERVER);
        if (in != null) {
            try {
                Config.RATE_EXP_SERVER = Byte.parseByte(in.trim());
                Logger.error("Exp mới: " + Config.RATE_EXP_SERVER);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Giá trị không hợp lệ");
            }
        }
    }

    private void hienThiFormNapVND() {
        JPanel p = new JPanel();
        JTextField fName = new JTextField(10), fVND = new JTextField(5);
        p.add(new JLabel("Tên nhân vật:")); p.add(fName);
        p.add(new JLabel("Số VNĐ:")); p.add(fVND);
        if (JOptionPane.showConfirmDialog(this, p, "Nạp VND", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
            napTien(fName.getText().trim(), fVND.getText().trim());
        }
    }

    private void napTien(String name, String vndStr) {
        try (Connection con = DatabaseManager.getConnection()) {
            int vnd = Integer.parseInt(vndStr);
            long cash = vnd * 2L * khuyenMaiNap;
            Player pl = Client.gI().getPlayer(name);
            if (pl == null) {
                JOptionPane.showMessageDialog(this, "Người chơi không online");
                return;
            }
            pl.getSession().cash += cash;
            pl.inventory.coupon += (vnd/1000) * khuyenMaiNap;
            try (PreparedStatement ps = con.prepareStatement(
                "UPDATE account SET cash=cash+?, danap=danap+? WHERE id=?")) {
                ps.setLong(1, cash); ps.setLong(2, cash);
                ps.setInt(3, pl.getSession().userId);
                ps.executeUpdate();
            }
            Service.gI().sendMoney(pl);
            JOptionPane.showMessageDialog(this,
                "Đã nạp " + cash + " cash cho " + pl.name);
        } catch (SQLException|NumberFormatException e) {
            Logger.logException(MenuAuto.class, e, "Lỗi nạp VND");
            JOptionPane.showMessageDialog(this, "Lỗi kết nối hoặc dữ liệu");
        }
    }

    private void capNhatNextNhiemVu() {
        JPanel p = new JPanel();
        JTextField fName = new JTextField(10), fTask = new JTextField(5);
        p.add(new JLabel("Tên nhân vật:")); p.add(fName);
        p.add(new JLabel("ID nhiệm vụ (1-31):")); p.add(fTask);
        if (JOptionPane.showConfirmDialog(this,p,"Next Nhiệm Vụ",
                JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            try {
                int tid = Integer.parseInt(fTask.getText().trim());
                Player pl = Client.gI().getPlayer(fName.getText().trim());
                if (pl==null) throw new Exception("Offline");
                while(pl.playerTask.taskMain.id<tid) 
                    TaskService.gI().sendNextTaskMain(pl);
                JOptionPane.showMessageDialog(this,
                    "Hoàn thành đến ID " + pl.playerTask.taskMain.id);
            } catch(Exception e){
                JOptionPane.showMessageDialog(this,e.getMessage());
            }
        }
    }
    
    private void hienThiFormDoiSuKien() {
        JPanel p = new JPanel();
        JTextField txtSK = new JTextField(5);
        p.add(new JLabel("ID Sự Kiện (1-8):")); p.add(txtSK);
        int res = JOptionPane.showConfirmDialog(this, p,
            "Đổi Sự Kiện", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            doiSuKien(txtSK.getText().trim());
        }
    }

    private void doiSuKien(String skStr) {
        try {
            byte sk = Byte.parseByte(skStr);
            // Reset tất cả flags
            EventManager.LUNNAR_NEW_YEAR = false;
            EventManager.INTERNATIONAL_WOMANS_DAY = false;
            EventManager.HALLOWEEN = false;
            EventManager.CHRISTMAS = false;
            EventManager.HUNG_VUONG = false;
            EventManager.TRUNG_THU = false;
            EventManager.TOP_UP = false;

            String tenEvent;
            switch (sk) {
                case 1:
                    EventManager.TRUNG_THU = true;
                    tenEvent = "Trung Thu";
                    break;
                case 2:
                    EventManager.TOP_UP = true;
                    tenEvent = "Mặc định";
                    break;
                case 3:
                    EventManager.LUNNAR_NEW_YEAR = true;
                    tenEvent = "Tết";
                    break;
                case 4:
                    EventManager.INTERNATIONAL_WOMANS_DAY = true;
                    tenEvent = "8 tháng 3";
                    break;
                case 5:
                    EventManager.HUNG_VUONG = true;
                    tenEvent = "Giỗ Tổ";
                    break;
                case 6:
                    EventManager.CHRISTMAS = true;
                    tenEvent = "Giáng Sinh";
                    break;
                case 7:
                    EventManager.HALLOWEEN = true;
                    tenEvent = "Halloween";
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "ID sự kiện không hợp lệ");
                    return;
            }

            // Load lại sự kiện theo flags
            EventManager.gI().init();

            Logger.error("Đổi sự kiện thành: " + tenEvent);
            Service.gI().sendThongBaoAllPlayer("Sự kiện " + tenEvent + " đang diễn ra");
            JOptionPane.showMessageDialog(this, "Đã đổi sự kiện thành: " + tenEvent);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID sự kiện không hợp lệ");
        }
    }

    private void hienThiFormBuffItem() {
        JPanel p=new JPanel();
        JTextField fName=new JTextField(),fIds=new JTextField(),
                   fOpts=new JTextField(),fVals=new JTextField(),fQty=new JTextField();
        Object[] msg={"Tên:",fName,"ID:",fIds,
            "Option:",fOpts,"Param:",fVals,"Số Lượng:",fQty};
        if(JOptionPane.showConfirmDialog(this,msg,"Buff Item",
           JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
           xuLyBuffItem(fName.getText().trim(),fIds.getText(),
               fOpts.getText(),fVals.getText(),fQty.getText().trim());
        }
    }

    private void xuLyBuffItem(String name,String ids,String opts,String vals,String qtyStr){
        try {
            Player pl=Client.gI().getPlayer(name);
            if(pl==null) throw new Exception("Offline");
            int qty=Integer.parseInt(qtyStr);
            String[] arrId=ids.split("-");
            String[] arrOpt=opts.split("-");
            String[] arrVal=vals.split("-");
            if(arrOpt.length!=arrVal.length) throw new Exception("Opts không khớp");
            String log="Buff:";
            for(String s:arrId){
                int id=Integer.parseInt(s.trim());
                if(id<0){
                    switch(id){
                        case -1:pl.inventory.gold=
                            Math.min(pl.inventory.gold+qty,Inventory.LIMIT_GOLD);
                            Service.gI().sendMoney(pl);log+=" vàng";break;
                        case -2:pl.inventory.gem=
                            Math.min(pl.inventory.gem+qty,2000000000);
                            Service.gI().sendMoney(pl);log+=" ngọc";break;
                        case -3:pl.inventory.ruby=
                            Math.min(pl.inventory.ruby+qty,2000000000);
                            Service.gI().sendMoney(pl);log+=" ngọc khóa";break;
                    }
                }else{
                    Item it=ItemService.gI().createNewItem((short)id);
                    for(int i=0;i<arrOpt.length;i++)
                        it.itemOptions.add(new Item.ItemOption(
                            Integer.parseInt(arrOpt[i]),
                            Integer.parseInt(arrVal[i])));
                    it.quantity=qty;
                    InventoryService.gI().addItemBag(pl,it);
                    log+=" x"+qty+" "+it.template.name;
                }
            }
            InventoryService.gI().sendItemBags(pl);
            NpcService.gI().createTutorial(pl,24,log);
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    private void hienThiFormBuffItemSKH() {
        JPanel p=new JPanel();JTextField fName=new JTextField(),fId=new JTextField(),
            fSKH=new JTextField(),fOpt=new JTextField(),fVal=new JTextField(),fQty=new JTextField();
        Object[] msg={"Tên:",fName,"ID:",fId,"SKH:",fSKH,"Option:",fOpt,"Param:",fVal,"Số Luọng:",fQty};
        if(JOptionPane.showConfirmDialog(this,msg,"Buff SKH",
           JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            xuLyBuffItemSKH(fName.getText().trim(),fId.getText().trim(),
                fSKH.getText().trim(),fOpt.getText().trim(),fVal.getText().trim(),fQty.getText().trim());
        }
    }

    private void xuLyBuffItemSKH(String name,String idStr,String skhStr,
        String optStr,String valStr,String qtyStr){try{
        Player pl=Client.gI().getPlayer(name);if(pl==null)throw new Exception("Offline");
        short id=Short.parseShort(idStr);int skh=Integer.parseInt(skhStr);
        int opt=Integer.parseInt(optStr),val=Integer.parseInt(valStr),qty=Integer.parseInt(qtyStr);
        Item it=ItemService.gI().createNewItem(id);
        it.itemOptions.add(new Item.ItemOption(skh,0));
        it.itemOptions.add(new Item.ItemOption(30,0));
        it.itemOptions.add(new Item.ItemOption(opt,val));
        switch(skh)
        {
            case 127:it.itemOptions.add(new Item.ItemOption(139,0));
                break;
            case 128:it.itemOptions.add(new Item.ItemOption(140,0));
                break;
            case 129:it.itemOptions.add(new Item.ItemOption(141,0));
                break;
            case 130:it.itemOptions.add(new Item.ItemOption(142,0));
                break;
            case 131:it.itemOptions.add(new Item.ItemOption(143,0));
                break;
            case 132:it.itemOptions.add(new Item.ItemOption(144,0));
                break;
            case 133:it.itemOptions.add(new Item.ItemOption(136,0));
                break;
            case 134:it.itemOptions.add(new Item.ItemOption(137,0));
                break;
            case 135:it.itemOptions.add(new Item.ItemOption(138,0));
                break;
        }
        it.quantity=qty;
        InventoryService.gI().addItemBag(pl,it);
        InventoryService.gI().sendItemBags(pl);
        NpcService.gI().createTutorial(pl,24,"Buff SKH x"+qty+" "+it.template.name);
        JOptionPane.showMessageDialog(this,"Buff SKH thành công");
    }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}}

    private static void batChongDDoS() {
        try {
            Runtime.getRuntime().exec(
                "cmd /c start \"\" \"E:\\Downloads\\v12\\NRO 2017\\chongddos\\run_chongddosvv.bat\"");
            Logger.log("Chống DDoS bật");
        } catch (IOException ex) {
            Logger.logException(MenuAuto.class, ex, "Không bật được DDoS");
        }
    }

private final ScheduledExecutorService scheduleExecutor = Executors.newSingleThreadScheduledExecutor();
private ScheduledFuture<?> scheduledTask;  // để huỷ khi đặt lại

private void lichHenBaoTri(JComboBox<Integer> comboGio, JComboBox<Integer> comboPhut) {
    int g = (int) comboGio.getSelectedItem();
    int p = (int) comboPhut.getSelectedItem();
    if (g < 0 || p < 0) {
        JOptionPane.showMessageDialog(this, "Giờ hoặc phút không hợp lệ");
        return;
    }

    File cfg = new File("logs", "server1.txt");
    cfg.getParentFile().mkdirs();
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(cfg))) {
        bw.write(g + "\n" + p);
    } catch (IOException ex) {
        Logger.logException(MenuAuto.class, ex, "Lỗi ghi file lịch bảo trì");
    }

    lblLichBaoTri.setText("<html>Bảo trì mỗi ngày<br/>" +
    String.format("%02d:%02d", g, p) +
    "</html>");
    if (scheduledTask != null && !scheduledTask.isCancelled()) {
        scheduledTask.cancel(false);
    }
    long initialDelay = computeInitialDelayInSeconds(g, p);
    scheduledTask = scheduleExecutor.scheduleAtFixedRate(() -> {
        MenuAuto.isRunning = true;
        Maintenance.gI().start(15);
    }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
}

private long computeInitialDelayInSeconds(int g, int p) {
    LocalDateTime now   = LocalDateTime.now();
    LocalDateTime first = now.withHour(g).withMinute(p).withSecond(0).withNano(0);
    if (first.isBefore(now)) {
        first = first.plusDays(1);
    }
    return Duration.between(now, first).getSeconds();
}

    private void napCauHinhBaoTri(JComboBox<Integer> comboGio, JComboBox<Integer> comboPhut) {
        File f=new File("server1.txt");if(!f.exists())return;
        try(BufferedReader br=new BufferedReader(new FileReader(f))){
            int g=Integer.parseInt(br.readLine());int p=Integer.parseInt(br.readLine());
            comboGio.setSelectedItem(g);comboPhut.setSelectedItem(p);
            lichHenBaoTri(comboGio,comboPhut);
        }catch(Exception ex){Logger.logException(MenuAuto.class,ex,"Lỗi đọc");}
    }

    private void hienThiXacNhanThoat() {
        if(JOptionPane.showConfirmDialog(this,"Thoát?","Xác nhận",JOptionPane.YES_NO_OPTION)
            ==JOptionPane.YES_OPTION) System.exit(0);
    }

    public static void runBatchFile(String workingDir) throws IOException {
        runBatchFile("run.bat", workingDir);
    }

    // MỚI: cho phép chỉ định fileName (restart.bat hoặc run.bat)
    public static void runBatchFile(String fileName, String workingDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
            "cmd", "/c",
            "start", "\"\"",
            "/D", workingDir,
            fileName
        );
        pb.directory(new File(workingDir));
        pb.redirectErrorStream(true);
        pb.start();
    }
}


