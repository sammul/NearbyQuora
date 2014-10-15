import java.util.ArrayList;
import java.util.HashMap;


public class Solution{
	
}


class QuadTree {
	public static final double initRadius = 1;
	public static final int	radiusMult = 2;
	ArrayList<Topic> s_topics;
	ArrayList<Topic> nw_topics;
	ArrayList<Topic> sw_topics;
	ArrayList<Topic> ne_topics;
	ArrayList<Topic> se_topics;
	int size;
	double cx, cy;
	QuadTree nw, ne, se, sw;
	QuadTree(ArrayList<Topic> topics, int depth, BoundRect br){
		size = topics.size();
		depth -= 1;
		if(depth==0){
			this.s_topics = topics;
			return;
		}
		this.s_topics = new ArrayList<Topic>();
		this.nw_topics = new ArrayList<Topic>();
		this.sw_topics = new ArrayList<Topic>();
		this.ne_topics = new ArrayList<Topic>();
		this.se_topics = new ArrayList<Topic>();
		
		//Get the bounding rect
		double l, t, r, b;
		if(br!=null){
			l = br.l;
			t = br.t;
			r = br.r;
			b = br.b;
		}
		else{
			l=Double.MAX_VALUE;
			t=Double.MAX_VALUE;
			r=Double.MIN_VALUE;
			b=Double.MIN_VALUE;
			for(Topic to: topics){
				l = Math.min(l, to.x);
				t = Math.min(t, to.y);
				r = Math.max(r, to.x);
				b = Math.max(b, to.y);
			}
		}
		//Cal the center
		this.cx = (l+r)/2;
		this.cy = (t+b)/2;
		
		for(Topic to: topics){
			boolean in_nw = to.x <=cx && to.y<=cy;
			boolean in_sw = to.x <=cx && to.y>=cy;
			boolean in_ne = to.x >=cx && to.y<=cy;
			boolean in_se = to.x >=cx && to.y>=cy;
			
			if(in_nw&&in_sw&&in_ne&&in_se)
				this.s_topics.add(to);
			else{
				if(in_nw)
					this.nw_topics.add(to);
				if(in_sw)
					this.sw_topics.add(to);
				if(in_se)
					this.se_topics.add(to);
				if(in_ne)
					this.ne_topics.add(to);
			}
		}
		if(nw_topics.size()>0)
			this.nw = new QuadTree(nw_topics,depth,new BoundRect(l,t,cx,cy));
		if(ne_topics.size()>0)
			this.ne = new QuadTree(ne_topics,depth,new BoundRect(cx,t,r,cy));
	    if(se_topics.size()>0)
	        this.se = new QuadTree(se_topics,depth,new BoundRect(cx,cy,r,b));
	    if(sw_topics.size()>0)
	        this.sw = new QuadTree(sw_topics,depth,new BoundRect(l,cy,cx,b));
	}
	void getPoints(double x, double y, double radius, HashMap<Topic, Double> tdis){
		Rect rect = new Rect(x, y, radius);
		getPointsFunction(x,y,radius,rect,tdis);

	}
	void getPointsFunction(double x, double y, double radius,Rect rect,HashMap<Topic, Double> tdis){
		for(Topic to : this.s_topics){
			double d = getDis(to,x,y);
			if(d<=radius)
				tdis.put(to, d);
		}
		if(this.nw!=null && rect.left <= this.cx && rect.top <= this.cy)
		      this.nw.getPointsFunction(x,y,radius,rect,tdis);
		if(this.sw!=null && rect.left <= this.cx && rect.bottom >= this.cy)
		      this.sw.getPointsFunction(x,y, radius, rect,tdis);
		if(this.ne!=null && rect.right >= this.cx && rect.top <= this.cy)
		      this.ne.getPointsFunction(x,y, radius, rect,tdis);
		if(this.se!=null && rect.right >= this.cx && rect.bottom >= this.cy)
		      this.se.getPointsFunction(x,y, radius, rect,tdis);

		return;
	}
	double getDis(Topic t, double x, double y){
		return getDistance(t.x,t.y,x,y);
	}
	double getDistance(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow((x1-x2),2)+Math.pow(y1-y2, 2));
	}
	ArrayList<Integer> topicQuery(int k, double x, double y){
		double radius = initRadius;
		int numTopics = Math.min(this.size, k);
		HashMap<Topic,Double> topicDis = new HashMap<Topic,Double>();
		while(topicDis.size()<numTopics){
			this.getPoints(x, y, radius, topicDis);
			radius *= radiusMult;
		}
		return null;
	}
	
	ArrayList<Integer> questionQuery(int k, double x, double y){
		double radius = initRadius;
		HashMap<Integer,Double> qDis = new HashMap<Integer,Double>();
		HashMap<Topic,Double> tDis = new HashMap<Topic,Double>();
		while(tDis.size()<this.size){
			this.getPoints(x, y, radius, tDis);
			for(Topic t: tDis.keySet()){
				for(int q: t.questions){
					if(qDis.containsKey(q))
						qDis.put(q, Math.min(qDis.get(q),tDis.get(t)));
					else
						qDis.put(q, tDis.get(t));
				}
			}
			if(qDis.size()>=k)
				break;
			radius *= radiusMult;
		}
		return null;
	}
}

class Topic{
	int id;
	double x;
	double y;
	ArrayList<Integer> questions;
	Topic(int id, double x, double y){
		this.id = id;
		this.x = x;
		this.y = y;
		this.questions = new ArrayList<Integer>();
	}
	void addQuestion(int qnum){
		this.questions.add(qnum);
	}
}

class Rect{
	double left, right, top, bottom;
	Rect(double x, double y, double radius){
		this.left = Math.max(0,x-radius);
		this.right = x+radius;
		this.top = Math.max(0, y-radius);
		this.bottom = y+radius;
	}
}

class BoundRect{
	double l,t,r,b;
	BoundRect(double l, double t, double r, double b){
		this.l = l;
		this.t = t;
		this.r = r;
		this.b = b;
	}
}