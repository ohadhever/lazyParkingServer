package adminUI;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import common.Reply;
import serverBL.authorizedUsersManagement.UserList;
import serverBL.authorizedUsersManagement.UserRestrictedInterface;
import serverBL.messagePassing.MessagePassing;


@SuppressWarnings("serial")
public class AdminUIFrame extends JFrame {
	public final int FRAME_HEIGHT = 600;
	public final int TEXT_AREA_ROWS = 40;
	public final int TEXT_AREA_COLS = 30;
	public final int BUTTONS_PANEL_WIDTH = 150;
	public final int OPERATOR_PERMISSION = 2;
	private UserList userList;
	private JTextArea messages;

	AdminUIFrame(String username, UserList userList, AdminLoginFrame parent, MessagePassing mp) {
		this.userList = userList;
		messages = new JTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLS);
		messages.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(messages);
		add(scrollPane, BorderLayout.CENTER);
		JPanel buttonsPanel = new JPanel(new GridLayout(5 , 1));
		AddOperatorPanel addOperatorPanel= new AddOperatorPanel();
		JButton addOperatorBtn = new JButton("Add Operator");
		addOperatorBtn.addActionListener(addOperatorPanel);
		buttonsPanel.add(addOperatorBtn);
		JButton removeOperatorBtn = new JButton("Remove Operator");
		removeOperatorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = JOptionPane.showInputDialog(AdminUIFrame.this, "username to remove:", "Remove Operator", JOptionPane.QUESTION_MESSAGE);
				if (username == null || username.equals(""))
					return;
				UserRestrictedInterface user = userList.getUser(username);
				if (user == null && !username.equals("")) {
					JOptionPane.showMessageDialog(AdminUIFrame.this, "username not found", "INVALID USERNAME", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (user.getPermission() > OPERATOR_PERMISSION) {
					JOptionPane.showMessageDialog(AdminUIFrame.this, "you do not have high enough permission to remove this user", "INVALID USER", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (JOptionPane.showConfirmDialog(AdminUIFrame.this, "Are you sure you want to delete " + username + "?", "Delete Operator", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
					if (userList.removeUser(username) == Reply.SUCCESS)
						JOptionPane.showMessageDialog(AdminUIFrame.this, username + " was removed successfully", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		buttonsPanel.add(removeOperatorBtn);
		
		JButton changePwBtn = new JButton("Change Password");
		changePwBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ChangePasswordDialog(username, userList, AdminUIFrame.this);
			}
			
		});
		buttonsPanel.add(changePwBtn);
		
		JButton shutDownBtn = new JButton("Shut Down Server");
		shutDownBtn.setBackground(Color.RED);
		shutDownBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(AdminUIFrame.this, "Are you sure you want to shut down the server?", "SHUT DOWN SERVER", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					mp.disconnect();
					System.exit(0);
				}
			}
		});
		buttonsPanel.add(shutDownBtn);
		
		JButton logoutBtn = new JButton("Logout");
		logoutBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(AdminUIFrame.this, "Are you sure you want to log out?", "Log out", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					parent.setVisible(true);
					AdminUIFrame.this.dispose();
				}
			}
		});
		buttonsPanel.add(logoutBtn);
		
		buttonsPanel.setPreferredSize(new Dimension(BUTTONS_PANEL_WIDTH, FRAME_HEIGHT));
		add(buttonsPanel, BorderLayout.EAST);
		add(new JLabel("EXCEPTIONS:"), BorderLayout.NORTH);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() { // does the same as the logout button
			@Override
			public void windowClosing(WindowEvent event) {
				if (JOptionPane.showConfirmDialog(AdminUIFrame.this, "Are you sure you want to log out?", "Log out", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					parent.setVisible(true);
					AdminUIFrame.this.dispose();
				}
			}
		});
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		ExceptionsLogger.adminLoggedIn(this);
	}
	
	void displayException(String msg) {
		messages.setForeground(Color.RED);
		messages.append(msg + "\n");
	}
	
	private class AddOperatorPanel extends JPanel implements ActionListener {
		private JTextField name;
		private JPasswordField password;
		
		public AddOperatorPanel() {
			setLayout(new GridLayout(2, 1));
			JPanel namePanel = new JPanel();
			namePanel.add(new JLabel("name: "));
			name = new JTextField(20);
			namePanel.add(name);
			add(namePanel);
			JPanel passwordPanel = new JPanel();
			passwordPanel.add(new JLabel("password: "));
			password = new JPasswordField(20);
			passwordPanel.add(password);
			add(passwordPanel);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (JOptionPane.showConfirmDialog(AdminUIFrame.this, this, "Add Operator", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				String nameStr = name.getText(); 
				if (!nameStr.matches("[A-Za-z]{2,}[ ]+[A-Za-z]{2,}")) {
					JOptionPane.showMessageDialog(AdminUIFrame.this, "enter the operator's first and last names. each name has to be at least 2 letters long and must include only letters", "INVALID NAME", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String names[] = nameStr.split(" ");
				if (names[0].length() + names[1].length() > 20) {
					JOptionPane.showMessageDialog(AdminUIFrame.this, "the combined length of the first and last names must not exceed 20 letters", "INVALID NAME", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String passwordStr = new String(password.getPassword());
				if (passwordStr.equals("")) {
					JOptionPane.showMessageDialog(AdminUIFrame.this, "the password must not be empty", "INVALID PASSWORD", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String username = userList.addUser(names[0], names[1], passwordStr, OPERATOR_PERMISSION);
				JOptionPane.showMessageDialog(AdminUIFrame.this, "username: " + username, "Operator Credentials", JOptionPane.INFORMATION_MESSAGE);
				name.setText("");
				password.setText("");
			}
		}
	}
}
