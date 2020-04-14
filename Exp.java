package os;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class buffer_pool {
	public static final int N = 3; // 定义缓冲池大小为3
	public static int empty = N, full = 0, mutex = 1, in = 0, out = 0;	//信号量
	public static int[] buffer = new int[N];	//缓冲池
	public static ArrayList<producer> pro_ready = new ArrayList<producer>(); // 生产者进程就绪队列
	public static ArrayList<consumer> con_ready = new ArrayList<consumer>(); // 消费者进程就绪队列
	public static ArrayList<producer> pro_clog = new ArrayList<producer>(); // 生产者进程阻塞队列
	public static ArrayList<consumer> con_clog = new ArrayList<consumer>(); // 消费者进程阻塞队列

	public static void printline() {
		System.out.println("--------------------------------");
	}

	public static void P_empty(producer p) {
		if (empty <= 0) {
			pro_ready.remove(p);
			pro_clog.add(p);
			System.out.println("缓冲池满，申请失败，进入生产进程阻塞队列");
			printline();
			p.judge = true;
		} else
			empty--;
	}

	public static void V_empty() {
		empty++;
	}

	public static void P_full(consumer c) {
		if (full <= 0) {
			con_ready.remove(c);
			con_clog.add(c);
			System.out.println("缓冲池空，申请失败，进入消费进程阻塞队列");
			printline();
			c.judge = true;
		}
		full--;
	}

	public static void V_full() {
		full++;
	}

	public static void P_mutex() {
		while (mutex <= 0);
		mutex--;
	}

	public static void V_mutex() {
		mutex++;
	}
}

class producer extends buffer_pool {
	char X;
	boolean judge = false; // 判断生产者进程是否阻塞

	public producer(char X) {
		this.X = X;
	}

	public static void ready(producer p) { // 生产者加入就绪队列
		pro_ready.add(p);
	}

	public static void wake(producer p) {
		p.judge = false;
		pro_ready.add(p);
		pro_clog.remove(p);
		System.out.println("生产者" + p.X + "被唤醒，加入生产者就绪队列");
		printline();
	}

	public static int inum() {
		if (in == 0)
			return 3;
		else
			return in;
	}

	public static void pro(producer p) {
		while (true) {
			System.out.print("生产者" + p.X + "申请生产产品---");
			P_empty(p);
			if (p.judge == true)
				break;
			P_mutex();
			System.out.println("申请成功，正在生产!");
			buffer[in] = 1;
			in = (in + 1) % N;
			V_mutex();
			V_full();
			pro_ready.remove(0);
			System.out.println("生产者" + p.X + "生产完毕，已投放进" + inum() + "号缓冲池!");
			printline();
			if (!con_clog.isEmpty())
				consumer.wake(con_clog.get(0));
			break;
		}
	}
}

class consumer extends buffer_pool {
	char X;
	boolean judge = false; // 判断消费者进程是否阻塞

	public consumer(char X) {
		this.X = X;
	}

	public static void ready(consumer c) { // 消费者加入就绪队列
		con_ready.add(c);
	}

	public static void wake(consumer c) {
		c.judge = false;
		con_ready.add(c);
		con_clog.remove(c);
		System.out.println("生产者" + c.X + "被唤醒，加入生产者就绪队列");
		printline();
	}

	public static int onum() {
		if (out == 0)
			return 3;
		else
			return out;
	}

	public static void con(consumer c) {
		while (true) {
			System.out.print("消费者" + c.X + "申请购买产品---");
			P_full(c);
			if (c.judge == true)
				break;
			P_mutex();
			System.out.println("申请成功，正在支付!");
			buffer[out] = 0;
			out = (out + 1) % N;
			V_mutex();
			V_empty();
			con_ready.remove(0);
			System.out.println(onum() + "号缓冲池产品" + "已被" + c.X + "消费者买走");
			printline();
			if (!pro_clog.isEmpty())
				producer.wake(pro_clog.get(0));
			break;
		}
	}
}

public class Exp extends buffer_pool {
	// 创建画框
	public static class MyFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		public static final String TITLE = "同步进程模拟-消费者与生产者";
		public static final int WIDTH = 750;
		public static final int HEIGHT = 380;

		public MyFrame() {
			super();
			initFrame();
		}

		private void initFrame() {
			setTitle(TITLE);
			setSize(WIDTH, HEIGHT);
			// 设置窗口关闭按钮的默认操作(点击关闭时退出进程)
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			// 把窗口位置设置到屏幕的中心
			setLocationRelativeTo(null);
			// 设置窗口的内容面板
			MyPanel panel = new MyPanel(this);
			setContentPane(panel);
		}

	}

	// 创建画布
	public static class MyPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("unused")
		private MyFrame frame;

		public MyPanel(MyFrame frame) {
			super();
			this.frame = frame;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			paintbuffer(g, buffer);
		}

		// 绘图
		public void paintbuffer(Graphics g, int[] x) {
			Graphics2D g2d = (Graphics2D) g.create();
			BasicStroke bs = new BasicStroke(3);
			g2d.setStroke(bs);

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setFont(new Font("宋体", 0, 18));
			g2d.drawString("缓冲池", 340, 60);
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawRect(140, 100, 450, 150);

			g2d.setFont(new Font("宋体", 0, 14));
			g2d.drawString("缓冲区1号", 180, 270);
			g2d.setColor(Color.cyan);
			g2d.drawRect(150, 110, 130, 130);
			g2d.setFont(new Font("宋体", 0, 14));
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawString("缓冲区2号", 335, 270);
			g2d.setColor(Color.GREEN);
			g2d.drawRect(300, 110, 130, 130);
			g2d.setFont(new Font("宋体", 0, 14));
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawString("缓冲区3号", 485, 270);
			g2d.setColor(Color.PINK);
			g2d.drawRect(450, 110, 130, 130);

			for (int i = 0; i < 3; i++) {
				if (x[i] == 0) {
					g2d.setColor(Color.black);
					g2d.setFont(new Font("宋体", 0, 18));
					g2d.drawString(i + 1 + "号缓冲区空", 20, 150 + i * 20);
				} else {
					g2d.setColor(Color.black);
					g2d.setFont(new Font("宋体", 0, 18));
					g2d.drawString(i + 1 + "号缓冲区满", 610, 150 + i * 20);
					g2d.setColor(Color.gray);
					BasicStroke bs1 = new BasicStroke(30);
					g2d.setStroke(bs1);
					g2d.drawOval(175 + i * 150, 135, 80, 80);
					g2d.setStroke(bs);
				}
			}

			g2d.dispose();
		}

	}

	// 延迟函数(1000ms)
	public static void delay() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// CPU调度就绪进程
	public static void cpu_pro() {
		if (pro_ready.isEmpty()) {
			System.out.print("生产进程已空\n");
			printline();
		} else
			producer.pro(pro_ready.get(0));
	}

	public static void cpu_con() {
		if (con_ready.isEmpty()) {
			System.out.print("消费进程已空\n");
			printline();
		} else
			consumer.con(con_ready.get(0));
	}

	public static void main(String[] args) {
		MyFrame frame = new MyFrame();
		frame.setVisible(true);

		// 创建十个生产者,十个消费者
		producer[] p = new producer[10];
		for (int i = 0; i < 10; i++)
			p[i] = new producer((char) ('A' + i));
		consumer[] c = new consumer[10];
		for (int i = 0; i < 10; i++)
			c[i] = new consumer((char) ('a' + i));

		// 生产者、消费者各自进入就绪队列
		for (int i = 0; i < 10; i++)
			producer.ready(p[i]);
		for (int i = 0; i < 10; i++)
			consumer.ready(c[i]);

		// 消费者与生产者进行十轮生产与消费(每轮按顺序尝试调度两个生产者进程与一个消费者进程)
		for (int i = 0; i < 10; i++) {
			Exp.delay();
			Exp.cpu_pro();
			frame.repaint();
			Exp.delay();
			Exp.cpu_pro();
			frame.repaint();
			Exp.delay();
			Exp.cpu_con();
			frame.repaint();
		}
	}

}
