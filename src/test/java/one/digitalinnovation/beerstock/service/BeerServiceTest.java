package one.digitalinnovation.beerstock.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repository.BeerRepository;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

	private static final long INVALID_BEER_ID = 1L;
	
	@Mock
	private BeerRepository beerRepository;
	
	private BeerMapper beerMapper = BeerMapper.INSTANCE;
	
	@InjectMocks
	private BeerService beerService;
	
	@Test
	void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
		//given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);
		
		//when
		Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
		Mockito.when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);
		
		//then
		BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);
		
		assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
		assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
		assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));
		
		//assertEquals(expectedBeerDTO.getId(), createdBeerDTO.getId()); - JUnit
		//assertEquals(expectedBeerDTO.getName(), createdBeerDTO.getName());
	}
	
	@Test
	void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() {
		//given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);
		
		//when
		Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));
		
		//then
		assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));
		
		
	}
	
	@Test
	void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException{
		//given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
			
		//when
		when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));
		
		//then
		BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());
		
		assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));
	}
	
	@Test
	void whenNotRegisteredBeerNameIsGivenThenThrowAnException (){
		//given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
			
		//when
		when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());
		
		//then
		assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedFoundBeerDTO.getName()));
	}
	
	@Test
	void whenListBeerIsCalledThenReturnAListOfBeers() {
		//given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
					
		//when
		when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));
		
		//then
		List<BeerDTO> foundListBeerDTO = beerService.listAll();
		
		assertThat(foundListBeerDTO, is(not(empty())));
		assertThat(foundListBeerDTO.get(0), is(equalTo(expectedFoundBeerDTO)));
	}
	
	@Test
	void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {			
		//when
		when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);
		
		//then
		List<BeerDTO> foundListBeerDTO = beerService.listAll();
		
		assertThat(foundListBeerDTO, is(empty()));
	}
	
	@Test
	void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException {
		//given
		BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);
		
		//when
		when(beerRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
		doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());
		
		//then
		beerService.deleteById(expectedDeletedBeerDTO.getId());
		
		verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
		verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
	}
	
	@Test
    void whenExclusionIsCalledWithInvalidIdThenExceptionShouldBeThrown() {
        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> beerService.deleteById(INVALID_BEER_ID));
    }
	
}
