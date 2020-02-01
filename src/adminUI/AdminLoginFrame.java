package adminUI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import serverBL.authorizedUsersManagement.UserList;
import serverBL.authorizedUsersManagement.UserRestrictedInterface;
import serverBL.messagePassing.MessagePassing;


@SuppressWarnings("serial")
public class AdminLoginFrame extends JFrame {
	public final int FRAME_HEIGHT = 300;
	public final int FRAME_WIDTH = 400;
	public final int VERTICAL_GAP = 20;
	public final int ADMIN_PERMISSION = 3;
	private JTextField username;
	private JPasswordField password;
	private UserList userList;
	private MessagePassing mp;
	
	public AdminLoginFrame(UserList userList, MessagePassing mp) {
		this.userList = userList;
		this.mp = mp;
		setLayout(new GridLayout(4, 1, 0, VERTICAL_GAP));
		setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		add(new JLabel("Admin Login", SwingConstants.CENTER));
		JPanel usernamePanel = new JPanel();
		usernamePanel.add(new JLabel("username: "));
		username = new JTextField(20);
		username.addActionListener(new LoginListener());
		usernamePanel.add(username);
		add(usernamePanel);
		JPanel passwordPanel = new JPanel();
		passwordPanel.add(new JLabel("password: "));
		password = new JPasswordField(20);
		password.addActionListener(new LoginListener());
		passwordPanel.add(password);
		add(passwordPanel);
		JButton loginBtn = new JButton("Login");
		loginBtn.addActionListener(new LoginListener());
		add(loginBtn);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				JOptionPane.showMessageDialog(AdminLoginFrame.this, "Only Admins can shut down the server");
			}
		});
		setVisible(true);
	}
	
	
	private class LoginListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			UserRestrictedInterface user = userList.getUser(username.getText());
			if (user == null || user.checkPassword(new String(password.getPassword())) < ADMIN_PERMISSION) {
				JOptionPane.showMessageDialog(AdminLoginFrame.this, "wrong username or password", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
			else {
				AdminLoginFrame.this.setVisible(false);
				password.setText("");
				new AdminUIFrame(user.getUserName(), userList, AdminLoginFrame.this, mp);
			}	
		}
	}
}
