public class Edge 
{
  int x, y, flow, capacity;
  Edge reverse;

  public Edge(int x, int y, int flow, int capacity)
  {
    this.x = x;
    this.y = y;
    this.flow = flow;
    this.capacity = capacity;
  }

  public void setReverse(Edge e)
  {
    reverse = e;
  }  
}
