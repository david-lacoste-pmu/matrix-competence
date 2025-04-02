package fr.pmu.matrix.competence.mapper;

import fr.pmu.matrix.competence.domain.Note;
import fr.pmu.matrix.competence.entity.NoteEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre les entités NoteEntity et les objets domaine Note
 */
@Component
public class NoteMapper {

    /**
     * Convertit une entité note en objet de domaine
     * 
     * @param entity Entité note
     * @return Objet de domaine note
     */
    public Note mapToNoteDomain(NoteEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Note note = new Note();
        note.setValeur(entity.getValeur());
        note.setLibelle(entity.getLibelle());
        return note;
    }
    
    /**
     * Convertit un objet de domaine en entité
     * 
     * @param domain Objet de domaine note
     * @return Entité note
     */
    public NoteEntity mapToNoteEntity(Note domain) {
        if (domain == null) {
            return null;
        }
        
        NoteEntity entity = new NoteEntity();
        entity.setValeur(domain.getValeur());
        entity.setLibelle(domain.getLibelle());
        return entity;
    }
}