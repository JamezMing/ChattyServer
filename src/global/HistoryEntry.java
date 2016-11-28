package global;
import requestsParser.Request;
@Deprecated
public class HistoryEntry {
	private Request req;
	private Integer index;
	
	public HistoryEntry(Request re, Integer in){
		req = re;
		index = in;
	}
	
	public Integer getIndex(){
		return index;
	}
	
	public Request getRequest(){
		return req;
	}

}
