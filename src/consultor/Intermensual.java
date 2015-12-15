/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consultor;

import java.util.Comparator;

/**
 *
 * @author ine031
 */
public class Intermensual {
    private String alias;
    private Double valor;
    private Double valorAbsoluto;
    
    public Intermensual(String alias, Double valor){
        this.alias = alias;
        this.valor = valor;
        if(valor < 0) this.valorAbsoluto = valor * -1;
        else this.valorAbsoluto = valor;
    }
    
    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the valor
     */
    public Double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(Double valor) {
        this.valor = valor;
    }

    /**
     * @return the valorAbsoluto
     */
    public Double getValorAbsoluto() {
        return valorAbsoluto;
    }

    /**
     * @param valorAbsoluto the valorAbsoluto to set
     */
    public void setValorAbsoluto(Double valorAbsoluto) {
        this.valorAbsoluto = valorAbsoluto;
    }
}

class IntermensualComparator implements Comparator<Intermensual> {
    public int compare(Intermensual v1, Intermensual v2) {
        int retorno = (int)(v1.getValor() - v2.getValor());
        return retorno;
    }
}