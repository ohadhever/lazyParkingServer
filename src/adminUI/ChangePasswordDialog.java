package adminUI;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import common.Reply;
import serverBL.authorizedUsersManagement.UserList;
import serverBL.authorizedUsersManagement.UserRestrictedInterface;



@SuppressWarnings("serial")
public class ChangePasswordDialog extends JDialog {
	
	public ChangePasswordDialog(String username, UserList userList, AdminUIFrame parent) {
		super(parent, "Change Password", true);
		changePWPanel panel = new changePWPanel(username, true);
		JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();
				if (ChangePasswordDialog.this.isVisible() && (e.getSource() == optionPane) && (prop.equals(JOptionPane.VALUE_PROPERTY)) && !optionPane.getValue().equals(JOptionPane.UNINITIALIZED_VALUE)){
					if (optionPane.getValue().equals(JOptionPane.OK_OPTION)) {
						optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE); // Without this line, only the FIRST click on the OK button would have invoked this listener (instead of every click).
						String changeeName = panel.getUsername();
						UserRestrictedInterface changer = userList.getUser(username);
						UserRestrictedInterface changee = userList.getUser(changeeName);
						if (!username.equals(changeeName)) {
							if (changee == null) {
								JOptionPane.showMessageDialog(parent, "username not found", "INVALID USERNAME", JOptionPane.ERROR_MESSAGE);
								return;
							}
							if (changer.getPermission() <= changee.getPermission()) {
								JOptionPane.showMessageDialog(parent, "you do not have high enough permission to remove this user", "INVALID USER", JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						if (userList.changePassword(changeeName, panel.getPW()) == Reply.SUCCESS)
							JOptionPane.showMessageDialog(parent, changeeName + "'s password was changed successfully", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);	
					}
					ChangePasswordDialog.this.dispose();
				}
			}
		});
		setContentPane(optionPane);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	
	private class changePWPanel extends JPanel {
		public final int NUM_OF_CHARACTERS_IN_TEXT_FIELD = 20;
		private JTextField username;
		private JPasswordField password;
		private String changerUsername;
		
		public changePWPanel(String username, boolean changeOthersPW) {
			changerUsername = username;
			setLayout(new GridLayout(3, 1));
			JPanel usernameLine = new JPanel();
			usernameLine.add(new JLabel("username: "));
			this.username = new JTextField(username, NUM_OF_CHARACTERS_IN_TEXT_FIELD);
			this.username.setEditable(false);
			usernameLine.add(this.username);
			add(usernameLine);
			if (changeOthersPW) {
				JCheckBox checkbox = new JCheckBox("change password for an operator", false);
				checkbox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e){
						changePWPanel.this.username.setEditable(checkbox.isSelected());
						if (!checkbox.isSelected())
							changePWPanel.this.username.setText(changerUsername);
					}
				});
				add(checkbox);
			}
			JPanel newPWPanel = new JPanel();
			newPWPanel.add(new JLabel("new password: "));
			password = new JPasswordField(NUM_OF_CHARACTERS_IN_TEXT_FIELD);
			newPWPanel.add(password);
			add(newPWPanel);
		}
		
		public String getUsername() {
			return username.getText();
		}
		public String getPW() {
			return new String(password.getPassword());
		}
	}
}
