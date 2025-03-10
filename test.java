package tp_tech;

import java.awt.EventQueue;

import javax.swing.JFrame;



public class test {
	private JFrame frame;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					test window = new test();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public test() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(200, 200, 701, 374);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
