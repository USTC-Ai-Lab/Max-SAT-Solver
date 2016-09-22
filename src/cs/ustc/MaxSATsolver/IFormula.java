package cs.ustc.MaxSATsolver;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * 存储cnf文件中读取的信息，及其相应的一些方法
 * @author ccding 
 * 2016年3月5日下午8:06:07
 */
public class IFormula{
	private  List<IClause> clauses; //所有的clauses
	private ILiteral[] vars; //formula的所有vars
	private List<ILiteral> literals;
	Set<ILiteral> visitedLits;
	Set<IClause> visitedClas;
	int nbVar, nbClas;


	/**
	 * 设置vars和clauses的容量
	 * @param nbvars
	 * @param nbclauses
	 */
	public void setUniverse(int nbvars, int nbclauses) {
		nbVar = nbvars;
		nbClas = nbclauses;
		vars = new ILiteral[nbvars];
		clauses = new ArrayList<>(nbclauses);
		literals = new ArrayList<>(nbvars*2);
		visitedLits = new HashSet<>();
		visitedClas = new HashSet<>();
		
	}
	
	/**
	 * 通过读取的id创建literal
	 * @param i
	 * @return
	 */	
	protected ILiteral getLiteral(int i) {
		ILiteral lit;
		int id = Math.abs(i) - 1; // maps from 1..n to 0..n-1
		if (vars[id] == null) {
			vars[id] = new ILiteral(id + 1);
		}
		if (i > 0) {
			lit = vars[id];
		} else {
			lit = vars[id].opposite();
		}
		return lit;
	}
	
	/**
	 * 通过vars添加clause
	 * @param vars
	 */
	public void addClause(ArrayList<ILiteral> lits) {
		// create the clause
		IClause clause = new IClause(lits);
		clauses.add(clause);
		for (ILiteral lit : lits) {
			lit.addClause(clause);
			lit.neighbors.addAll(lits);
			lit.neighbors.remove(lit);
			lit.degree += lits.size()-1;
			lit.initDegree = lit.degree;
		} 
	}
	
	/**
	 * get vars 
	 * @return vars
	 */
	public ILiteral[] getvars(){
		return vars;
	}
	public List<ILiteral> getLiterals() {
		return literals;
	}
	/**
	 * get clauses	
	 * @return clauses
	 */
	public List<IClause> getClauses() {
		return this.clauses;
	}
	/**
	 * set literals
	 */
	public void setLiterals(){
		for (int i = 0; i < vars.length; i++) {
			if(vars[i]!=null){
				literals.add(vars[i]);
				literals.add(vars[i].opposite);
			}
		}
	}
	
	/**
	 * get independent set 
	 * first, find vertexes set covers all edges
	 * then, the complementary set of all vertexes is independent set
	 * @return independent set
	 */
	public Set<ILiteral> getIndependentSet(double randomCoef){
		if(Math.random()>randomCoef)
			Collections.sort(literals);
		Set<ILiteral> vertexCover = new HashSet<>();
		Set<IClause> coverEdges = new HashSet<>();
		Set<ILiteral> independentSet = new HashSet<>(literals);
		for(int i=0; i<literals.size(); i++){
			if(coverEdges.size()==clauses.size())
				break;
			vertexCover.add(literals.get(i));
			coverEdges.addAll(literals.get(i).getClas());
			
		}
		independentSet.removeAll(vertexCover);
		return independentSet;
//		return vertexCover;
		
	}
	
	public void setFormulaByGroup(Group group){
		for(int i=0; i<group.agents.size(); i++){
			visitedClas.addAll(group.agents.get(i).visitedClas);
		}
		clauses.removeAll(visitedClas);
		visitedLits.addAll(group.agents);
		literals.removeAll(group.agents);
	}
	
	
	public List<ILiteral> getUnvisitedLits(){
		List<ILiteral> unvisitedLits = new ArrayList<>();
		for(ILiteral l: literals){
			if(!l.forbid){
				unvisitedLits.add(l.getClas().size()>l.opposite.getClas().size()
						? l:l.opposite);
				l.forbid = true;
				l.opposite.forbid = true;
			}
		}
		return unvisitedLits;
	}
	
	
	
	public void reset(){
		clauses.addAll(visitedClas);
		visitedClas.clear();
		literals.addAll(visitedLits);
		visitedLits.clear();
		for(ILiteral l: literals){
			l.forbid = false;
			l.degree = l.initDegree;
			l.getClas().addAll(l.visitedClas);
			l.visitedClas.clear();
		}
		for(IClause c: clauses){
			c.unsatLitsNum = 0;
		}
	}
	
	
	
}
