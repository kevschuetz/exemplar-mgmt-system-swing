package view.frames.mainFrame;

import model.entities.User;
import model.httpclients.UserClient;
import view.listeners.mainframe.communityTap.AddUserListener;
import view.panels.mainFrame.CommunityTab;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddMemberFrame extends JFrame{
        private CommunityTab tab;
        private JPanel parentPanel = new JPanel();
        private JTextField searchField = new JTextField();
        private JButton searchButton = new JButton("Search");
        private JPanel searchPanel = new JPanel();
        private JList<User> searchList = new JList<User>();
        private List<User> foundUsers;
        private JButton addButton = new JButton("Add");
        private JScrollPane listScroller;
        private UserClient client = new UserClient();
        private AddUserListener listener;


        public AddMemberFrame(){
            setLayout(new GridLayout(1,1));
            parentPanel.setLayout(new GridBagLayout());
            parentPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("New User"),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            initializeComponents();
            addComponents();
            addActionListeners();
            getRootPane().setDefaultButton(searchButton);
        }

        void initializeComponents(){
            searchList = new JList<User>(fetchUsers(""));
            searchList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            searchList.setVisibleRowCount(-1);
            listScroller = new JScrollPane(searchList);
        }

        void addComponents(){
            searchPanel.setLayout(new GridLayout(1,2));
            searchPanel.add(searchField);
            searchPanel.add(searchButton);

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.gridy = 0;
            c.gridy = 0;
            c.weighty = 0.05;
            c.weightx = 1;
            parentPanel.add(searchPanel, c);

            c.gridy = 1;
            c.weighty = 0.9;
            parentPanel.add(listScroller, c);

            c.gridy = 2;
            c.weighty = 0.05;
            parentPanel.add(addButton, c);

            add(parentPanel);
        }

        User[] fetchUsers(String s){
            List<User> list = client.searchUsers(s);
            User[] array = new User[list.size()];
            int i = 0;
            for(User u : list){
                array[i] = u;
                i++;
            }
            return array;
        }

        void addActionListeners(){
            searchButton.addActionListener((x)->{
                User[] users = fetchUsers(searchField.getText());
                searchList.setListData(users);
            });
            addButton.addActionListener((x)->{
                User u = searchList.getSelectedValue();
                if(u!=null)listener.addingRequested(u);
            });
        }

        public CommunityTab getTab() {
            return tab;
        }

        public void setTab(CommunityTab tab) {
            this.tab = tab;
        }

        public void setListener(AddUserListener listener) {
            this.listener = listener;
        }
}

