package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import net.engineeringdigest.journalApp.exception.ResourceNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    public JournalEntry saveEntry(JournalEntry journalEntry, String username) {
        journalEntry.setDate(LocalDateTime.now());
        JournalEntry savedEntry = journalEntryRepository.save(journalEntry);
        
        User user = userService.findByUserName(username);
        if (user != null) {
            if (user.getJournalEntries() == null) {
                user.setJournalEntries(new ArrayList<>());
            }
            user.getJournalEntries().add(savedEntry);
            
            userService.saveEntry(user);
        }
        
        return savedEntry;
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    public boolean deleteById(ObjectId id) {
        if(!journalEntryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Journal not found with id: " + id);
        }
        journalEntryRepository.deleteById(id);
        return true;
    }

    public JournalEntry updateJournalById(ObjectId id, JournalEntry journalEntry) {
        journalEntry.setId(id);
        return journalEntryRepository.save(journalEntry);
    }
}



//controller -> service -> repository -> database -> service -> controller