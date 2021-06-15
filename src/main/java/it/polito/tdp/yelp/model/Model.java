package it.polito.tdp.yelp.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {

	private YelpDao dao;
	private SimpleDirectedWeightedGraph<Business, DefaultWeightedEdge> grafo;
	private Map<String,Business> idMap;
	private Map<String,Business> vertici;
	private List<Business> percorsoMigliore;
	private Integer lunghezza;
	
	public Model() {
		dao=new YelpDao();
		idMap=new HashMap<String,Business>();
		vertici=new HashMap<String,Business>();
		
		this.dao.getAllBusiness(idMap);
	}
	
	public List<String> getCitta(){
		return this.dao.getAllCitta();
	}

	public void creaGrafo(Integer anno, String citta) {
		grafo=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, this.dao.getVertici(anno, citta, idMap, vertici));
		
		for(Archi a:this.dao.getArchi(anno, citta, vertici)) {
			double peso;
			if(a.peso>0) {
				peso=a.getPeso();
				Graphs.addEdge(grafo, a.getB2(), a.getB1(), peso);
			}
			else{
				peso=Math.abs(a.getPeso());
				Graphs.addEdge(grafo, a.getB1(), a.getB2(), peso);
			}
		}
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public boolean getGrafo() {
		if(grafo==null) {
			return false;
		}
		else
			return true;
	}

	public Business doLocaleMigliore() {
		double peso=Integer.MIN_VALUE;
		Business migliore=null;
		
		for(Business b:grafo.vertexSet()) {
			double sommaentranti=0;
			double sommauscenti=0;
			for(DefaultWeightedEdge e:this.grafo.outgoingEdgesOf(b)) {
				sommauscenti+=this.grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e:this.grafo.incomingEdgesOf(b)) {
				sommaentranti+=this.grafo.getEdgeWeight(e);
			}
			
			if((sommaentranti-sommauscenti)>peso) {
				peso=(sommaentranti-sommauscenti);
				migliore=b;
			}
		}
		
		return migliore;
	}
	
	public Set<Business> getBusiness(){
		return this.grafo.vertexSet();
	}

	public List<Business> calcolaPercorso(Business b, double x) {
		percorsoMigliore=new LinkedList<Business>();
		lunghezza=Integer.MAX_VALUE;
		
		List<Business> parziale=new LinkedList<Business>();
		parziale.add(b);
		
		cerca(parziale,x,b);
		
		return percorsoMigliore;
	}

	private void cerca(List<Business> parziale, double x, Business business) {
		if(business==this.doLocaleMigliore()) {
			if(parziale.size()<lunghezza) {
				percorsoMigliore=new LinkedList<>(parziale);
				lunghezza=parziale.size();
			}
			return;
		}
		
		for(DefaultWeightedEdge e:this.grafo.outgoingEdgesOf(business)) {
			double peso=this.grafo.getEdgeWeight(e);
			Business b=grafo.getEdgeTarget(e);
			if(peso>=x) {
				parziale.add(b);
				cerca(parziale,x,b);
				parziale.remove(parziale.size()-1);
			}
		}
	}
}
