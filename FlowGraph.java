import java.util.*;
public abstract class FlowGraph extends Graph{
	public abstract Set<String> def(Node node);
	public abstract Set<String> use(Node node);
	//public abstract boolean isMove(Node node);
	//public void show(java.io.PrintStream out);
}