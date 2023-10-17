package nz.ac.auckland.se206.gpt;

/** Represents a chat message in the conversation. */
public class ChatMessage {

  private String role;
  private String content;

  /**
   * Constructs a new ChatMessage object with the specified role and content.
   *
   * @param role the role of the message (e.g., "user", "assistant")
   * @param content the content of the message
   */
  public ChatMessage(String role, String content) {
    this.role = role;
    this.content = content;
  }

  /**
   * Returns the role of the chat message.
   *
   * @return the role.
   */
  public String getRole() {
    return role;
  }

  /**
   * Returns the content of the chat message.
   *
   * @return the content in the ChatMessage.
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the content of a ChatMessage.
   *
   * @param content the content to add to a ChatMessage.
   */
  public void setContent(String content) {
    this.content = content;
  }
}
