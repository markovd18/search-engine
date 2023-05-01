package cz.zcu.kiv.nlp.ir.data;

import cz.zcu.kiv.nlp.ir.index.Indexable;

/**
 * Created by Tigi on 8.1.2015.
 *
 * Rozhraní reprezentuje dokument, který je možné indexovat a vyhledávat.
 *
 * Implementujte toto rozhranní.
 *
 * Pokud potřebujete můžete do rozhranní přidat metody, ale signaturu
 * stávajících metod neměnte.
 *
 */
public interface Document extends Indexable {

    /**
     * Unikátní id dokumentu
     * 
     * @return id dokumentu
     */
    long getId();

}
