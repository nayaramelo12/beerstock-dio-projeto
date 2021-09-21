package one.digitalinnovation.beerstock.service;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repository.BeerRepository;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

	private final BeerRepository beerRepository;
	private final BeerMapper beerMapper = BeerMapper.INSTANCE;
}
