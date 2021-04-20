public class Author
{
  private int id;
  private String name;

  public Author(int id, String name)
  {
    this.id = id;
    this.name = name;
  }

  public Author(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public int getId()
  {
    return id;
  }
}
