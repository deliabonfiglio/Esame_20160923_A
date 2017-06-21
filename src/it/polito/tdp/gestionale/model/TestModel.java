package it.polito.tdp.gestionale.model;

public class TestModel {

	public static void main(String[] args) {

		Model model = new Model();
		model.generaGrafo();
		int counter=0;
	for(Integer i:	model.getStatCorsi()){
		System.out.format("Numero di studenti che seguono %d corsi : %d\n", counter, i);
		counter++;
	}
	
	System.out.println(	model.findMinimalSet());
}
}
