package chat.client.view;

import static java.util.Objects.requireNonNull;

import chat.client.controller.ChatController;
import chat.client.model.ChatClientModel;
import chat.client.model.events.ChatEvent;
import chat.client.model.events.MessageAddedEvent;
import chat.client.view.chatview.ChatCellRenderer;
import chat.client.view.chatview.ChatEntry;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/**
 * The main view of the chat user interface. It provides and connects all graphical elements
 * that are necessary for a chat application. It provides a user a screen for logging in, and
 * in case of success shows afterwards the necessary elements for writing and reading chat messages.
 */
public class ChatFrame extends JFrame implements PropertyChangeListener {

  @Serial
  private static final long serialVersionUID = 1L;

  private static final String LOGIN_CARD = "login";
  private static final String CHAT_CARD = "chat";

  private final ChatClientModel model;
  private final ChatController controller;

  private CardLayout layout;
  private JTextField nickName;
  private DefaultListModel<ChatEntry> listModel;
  private JTextArea inputArea;
  private JScrollPane scrollPane;

  /**
   * Create a new graphical view that contains all necessary elements for chatting with .
   *
   * @param model      The {@link ChatClientModel} that handles the logic of the game.
   * @param controller The {@link ChatController} that validates and forwards any user input.
   */
  public ChatFrame(ChatController controller, ChatClientModel model) {
    super("Chat Client");

    this.controller = requireNonNull(controller);
    this.model = requireNonNull(model);

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    initializeWidgets();
    addEventListeners();
    createView();

    pack();
  }

  /**
   * Instantiate all Swing widgets and specify config options where appropriate.
   */
  private void initializeWidgets() {
    layout = new CardLayout();

    nickName = new JTextField(20);

    listModel = new DefaultListModel<>();
    listModel.addAll(model.getMessages());

    JList<ChatEntry> chatList = new JList<>(listModel);
    chatList.setCellRenderer(new ChatCellRenderer());
    scrollPane = new JScrollPane(chatList);
    scrollPane.setPreferredSize(new Dimension(150, 300));
    scrollPane.setMaximumSize(new Dimension(150, 300));
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

    inputArea = new JTextArea(3, 50);
    inputArea.setLineWrap(true);
    inputArea.setWrapStyleWord(true);
    inputArea.setBorder(new JTextField().getBorder());
  }

  /**
   * Add event listeners to all widgets wherever needed and let them execute the respective action.
   */
  private void addEventListeners() {
    nickName.addActionListener(e -> controller.login(nickName.getText().trim()));


    inputArea.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() != KeyEvent.VK_ENTER) {
          return;
        }
        event.consume();
        controller.postMessage(inputArea.getText());
        inputArea.setText(null);
      }
    });
  }

  /**
   * Set up the view in a way that is finally shown to the user.
   */
  private void createView() {
    JPanel panel = new JPanel(layout);
    setContentPane(panel);

    // Panel for the login view
    JPanel login = new JPanel();
    add(login, LOGIN_CARD);

    login.add(new JLabel("Login with your nick name:"));
    login.add(nickName);

    // Panel for the chat view (shown when successfully logged in)
    JPanel chatPanel = new JPanel(new GridBagLayout());
    add(chatPanel, CHAT_CARD);

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.insets = new Insets(5, 5, 5, 5);
    constraints.fill = GridBagConstraints.BOTH;
    constraints.weighty = 0.9;
    chatPanel.add(scrollPane, constraints);

    constraints = new GridBagConstraints();
    constraints.insets = new Insets(5, 5, 5, 5);
    constraints.gridy = 1;
    chatPanel.add(inputArea, constraints);
  }

  @Override
  public void dispose() {
    super.dispose();
    model.removePropertyChangeListener(this);
    //controller.dispose();
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        handleModelUpdate(event);
      }
    });
  }

  /**
   * The observable (= model) just published that it has changed its state. The GUI needs to be
   * updated accordingly here.
   *
   * @param event The {@link PropertyChangeEvent} that was fired by the model.
   */
  private void handleModelUpdate(PropertyChangeEvent event) {
    ChatEvent newValue = (ChatEvent) event.getNewValue();
    String eventName = newValue.getName();

    switch (eventName) {
      case "LoggedInEvent":
        showChat();
        break;
      case "LoginFailedEvent":
        showErrorMessage("Login already in use");
        break;
      case "MessageAddedEvent":
        MessageAddedEvent messageAddedEvent = (MessageAddedEvent) newValue;
        ChatEntry message = messageAddedEvent.getMessage();
        listModel.add(listModel.getSize(),message);
        break;
      case "MessageRemovedEvent":
        // TODO
        break;
      default:
        throw new IllegalArgumentException("Unknown type of event.");
    }
  }

  /**
   * Show the login view to the user
   */
  private void showLogin() {
    showCard(LOGIN_CARD);
  }

  /**
   * Show the chat view to the user
   */
  private void showChat() {
    showCard(CHAT_CARD);
  }

  private void showCard(String card) {
    layout.show(getContentPane(), card);
  }

  /**
   * Displays an error message to the user.
   *
   * @param message The message to be displayed
   */
  public void showErrorMessage(String message) {
    JOptionPane.showMessageDialog(null, message, "Uh-oh!", JOptionPane.INFORMATION_MESSAGE);
  }
}
