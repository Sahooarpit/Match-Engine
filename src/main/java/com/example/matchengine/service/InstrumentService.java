package com.example.matchengine.service;

import com.example.matchengine.Instrument;
import com.example.matchengine.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

    public Instrument addInstrument(String ticker, String description) {
        Instrument instrument = new Instrument(ticker, description);
        return instrumentRepository.save(instrument);
    }
}