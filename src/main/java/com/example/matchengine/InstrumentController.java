package com.example.matchengine;

import com.example.matchengine.api.InstrumentsApi;
import com.example.matchengine.model.InstrumentRequest;
import com.example.matchengine.repository.InstrumentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InstrumentController implements InstrumentsApi {

    private final InstrumentRepository instrumentRepository;

    @Override
    public ResponseEntity<com.example.matchengine.model.Instrument> addInstrument(@Valid InstrumentRequest instrumentRequest) {
        Instrument instrument = new Instrument(instrumentRequest.getTicker(), instrumentRequest.getDescription());
        instrumentRepository.save(instrument);

        com.example.matchengine.model.Instrument response = new com.example.matchengine.model.Instrument();
        response.setTicker(instrument.getTicker());
        response.setDescription(instrument.getDescription());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
