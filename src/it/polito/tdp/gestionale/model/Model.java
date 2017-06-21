package it.polito.tdp.gestionale.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.*;

import it.polito.tdp.gestionale.db.DidatticaDAO;

public class Model {

	private List<Corso> corsi;
	private List<Studente> studenti;
	private DidatticaDAO didatticaDAO;
	private Map<Integer, Studente> mapStudenti;
	private SimpleGraph<Nodo, DefaultEdge> graph;

	public Model() {
		this.graph= new SimpleGraph<Nodo, DefaultEdge>(DefaultEdge.class);
		didatticaDAO = new DidatticaDAO();
		mapStudenti= new HashMap<Integer, Studente>();
	}
	
	
	private List<Corso> getTuttiCorsi() {
		if(corsi ==null){
			corsi = didatticaDAO.getTuttiICorsi();
			this.getTuttiStudenti();	//trucco per assicurarsi che la mappa sia creata			
		
		for(Corso c: corsi){
			//riempie la lista di studenti iscritti al corso x ogni corso
			didatticaDAO.setStudentiIscrittiAlCorso(c, mapStudenti);
		}
		}
		return corsi;
	}

	private List<Studente> getTuttiStudenti() {
		if(studenti ==null){
			studenti = didatticaDAO.getTuttiStudenti();
		
			for(Studente s: studenti)
				mapStudenti.put(s.getMatricola(),s);
		}
		return studenti;
	}
	
	public void generaGrafo(){
		studenti = getTuttiStudenti();
		corsi = getTuttiCorsi();
		
		Graphs.addAllVertices(graph, studenti);
		Graphs.addAllVertices(graph, corsi);
		
		//aggiungiamo gli archi
		for(Corso ctemp: corsi){
			for(Studente stemp: ctemp.getStudenti()){
				graph.addEdge(ctemp, stemp);				
			}
		}
		System.out.println("Numero vertici: "+graph.vertexSet().size());
		System.out.println("Numero archi: "+graph.edgeSet().size());

	}
	
	public List<Integer> getStatCorsi(){
		List<Integer> statCorsi = new ArrayList<Integer>();
		//inizializzo struttura dati dove salvo le statistiche
		for(int i=0; i<corsi.size()+1; i++){
			statCorsi.add(0);
		}
		//aggiorno le statistiche
		for(Studente s: studenti){
			int numCorsi= Graphs.neighborListOf(graph,s).size();
			int counter = statCorsi.get(numCorsi);
			counter ++;
			statCorsi.set(numCorsi, counter);
		}
		return statCorsi;
		
	}

	public List<Corso> findMinimalSet(){
		List<Corso> parziale= new ArrayList<Corso>();
		List<Corso> best= new ArrayList<Corso>();
		
		//non è necessario un livello xk termina da solo quando fa tutte le possibili soluzioni
		this.recursive(parziale, best);
		return best;
	}
	
	private void recursive(List<Corso> parziale, List<Corso> best){
		System.out.println(parziale.toString());
		//verifico che la parziale sia buona
		
		HashSet<Studente> studs= new HashSet<Studente>(this.getTuttiStudenti());
		for(Corso c1: parziale){
			//agli studenti rimuovo quelli iscritti ai corsi
			studs.removeAll(c1.getStudenti());
		}
		
		if(studs.isEmpty()){ //se è vuota allora ha senso controllare se è meglio di best
			if(best.isEmpty())//best vuota = mai creata ==> inserisco parziale
				best.addAll(parziale);
			if(parziale.size()<best.size()){ // se parziale megio di best xk in dim piu piccola
				best.clear();				//svuoto best e metto parziale in best
				best.addAll(parziale);				
			}
		}
		
		//genera tutte le sequenze i cui codici del corso sono maggiori dell'ultimo inserito in parziale, xk l'albero dei corsi è ordinato
		for(Corso c: this.getTuttiCorsi()){
			if(parziale.isEmpty() || c.compareTo(parziale.get(parziale.size()-1))>0){
				parziale.add(c);
				recursive(parziale, best);
				parziale.remove(c);
			}
		}
	}
}
