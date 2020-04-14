package os;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class buffer_pool {
	public static final int N = 3; // ���建��ش�СΪ3
	public static int empty = N, full = 0, mutex = 1, in = 0, out = 0;	//�ź���
	public static int[] buffer = new int[N];	//�����
	public static ArrayList<producer> pro_ready = new ArrayList<producer>(); // �����߽��̾�������
	public static ArrayList<consumer> con_ready = new ArrayList<consumer>(); // �����߽��̾�������
	public static ArrayList<producer> pro_clog = new ArrayList<producer>(); // �����߽�����������
	public static ArrayList<consumer> con_clog = new ArrayList<consumer>(); // �����߽�����������

	public static void printline() {
		System.out.println("--------------------------------");
	}

	public static void P_empty(producer p) {
		if (empty <= 0) {
			pro_ready.remove(p);
			pro_clog.add(p);
			System.out.println("�������������ʧ�ܣ���������������������");
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
			System.out.println("����ؿգ�����ʧ�ܣ��������ѽ�����������");
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
	boolean judge = false; // �ж������߽����Ƿ�����

	public producer(char X) {
		this.X = X;
	}

	public static void ready(producer p) { // �����߼����������
		pro_ready.add(p);
	}

	public static void wake(producer p) {
		p.judge = false;
		pro_ready.add(p);
		pro_clog.remove(p);
		System.out.println("������" + p.X + "�����ѣ����������߾�������");
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
			System.out.print("������" + p.X + "����������Ʒ---");
			P_empty(p);
			if (p.judge == true)
				break;
			P_mutex();
			System.out.println("����ɹ�����������!");
			buffer[in] = 1;
			in = (in + 1) % N;
			V_mutex();
			V_full();
			pro_ready.remove(0);
			System.out.println("������" + p.X + "������ϣ���Ͷ�Ž�" + inum() + "�Ż����!");
			printline();
			if (!con_clog.isEmpty())
				consumer.wake(con_clog.get(0));
			break;
		}
	}
}

class consumer extends buffer_pool {
	char X;
	boolean judge = false; // �ж������߽����Ƿ�����

	public consumer(char X) {
		this.X = X;
	}

	public static void ready(consumer c) { // �����߼����������
		con_ready.add(c);
	}

	public static void wake(consumer c) {
		c.judge = false;
		con_ready.add(c);
		con_clog.remove(c);
		System.out.println("������" + c.X + "�����ѣ����������߾�������");
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
			System.out.print("������" + c.X + "���빺���Ʒ---");
			P_full(c);
			if (c.judge == true)
				break;
			P_mutex();
			System.out.println("����ɹ�������֧��!");
			buffer[out] = 0;
			out = (out + 1) % N;
			V_mutex();
			V_empty();
			con_ready.remove(0);
			System.out.println(onum() + "�Ż���ز�Ʒ" + "�ѱ�" + c.X + "����������");
			printline();
			if (!pro_clog.isEmpty())
				producer.wake(pro_clog.get(0));
			break;
		}
	}
}

public class Exp extends buffer_pool {
	// ��������
	public static class MyFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		public static final String TITLE = "ͬ������ģ��-��������������";
		public static final int WIDTH = 750;
		public static final int HEIGHT = 380;

		public MyFrame() {
			super();
			initFrame();
		}

		private void initFrame() {
			setTitle(TITLE);
			setSize(WIDTH, HEIGHT);
			// ���ô��ڹرհ�ť��Ĭ�ϲ���(����ر�ʱ�˳�����)
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			// �Ѵ���λ�����õ���Ļ������
			setLocationRelativeTo(null);
			// ���ô��ڵ��������
			MyPanel panel = new MyPanel(this);
			setContentPane(panel);
		}

	}

	// ��������
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

		// ��ͼ
		public void paintbuffer(Graphics g, int[] x) {
			Graphics2D g2d = (Graphics2D) g.create();
			BasicStroke bs = new BasicStroke(3);
			g2d.setStroke(bs);

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setFont(new Font("����", 0, 18));
			g2d.drawString("�����", 340, 60);
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawRect(140, 100, 450, 150);

			g2d.setFont(new Font("����", 0, 14));
			g2d.drawString("������1��", 180, 270);
			g2d.setColor(Color.cyan);
			g2d.drawRect(150, 110, 130, 130);
			g2d.setFont(new Font("����", 0, 14));
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawString("������2��", 335, 270);
			g2d.setColor(Color.GREEN);
			g2d.drawRect(300, 110, 130, 130);
			g2d.setFont(new Font("����", 0, 14));
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawString("������3��", 485, 270);
			g2d.setColor(Color.PINK);
			g2d.drawRect(450, 110, 130, 130);

			for (int i = 0; i < 3; i++) {
				if (x[i] == 0) {
					g2d.setColor(Color.black);
					g2d.setFont(new Font("����", 0, 18));
					g2d.drawString(i + 1 + "�Ż�������", 20, 150 + i * 20);
				} else {
					g2d.setColor(Color.black);
					g2d.setFont(new Font("����", 0, 18));
					g2d.drawString(i + 1 + "�Ż�������", 610, 150 + i * 20);
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

	// �ӳٺ���(1000ms)
	public static void delay() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// CPU���Ⱦ�������
	public static void cpu_pro() {
		if (pro_ready.isEmpty()) {
			System.out.print("���������ѿ�\n");
			printline();
		} else
			producer.pro(pro_ready.get(0));
	}

	public static void cpu_con() {
		if (con_ready.isEmpty()) {
			System.out.print("���ѽ����ѿ�\n");
			printline();
		} else
			consumer.con(con_ready.get(0));
	}

	public static void main(String[] args) {
		MyFrame frame = new MyFrame();
		frame.setVisible(true);

		// ����ʮ��������,ʮ��������
		producer[] p = new producer[10];
		for (int i = 0; i < 10; i++)
			p[i] = new producer((char) ('A' + i));
		consumer[] c = new consumer[10];
		for (int i = 0; i < 10; i++)
			c[i] = new consumer((char) ('a' + i));

		// �����ߡ������߸��Խ����������
		for (int i = 0; i < 10; i++)
			producer.ready(p[i]);
		for (int i = 0; i < 10; i++)
			consumer.ready(c[i]);

		// �������������߽���ʮ������������(ÿ�ְ�˳���Ե������������߽�����һ�������߽���)
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
