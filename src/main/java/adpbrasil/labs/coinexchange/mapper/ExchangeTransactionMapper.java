package adpbrasil.labs.coinexchange.mapper;

import adpbrasil.labs.coinexchange.dto.ExchangeTransactionDto;
import adpbrasil.labs.coinexchange.model.ExchangeTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExchangeTransactionMapper {
    ExchangeTransactionMapper INSTANCE = Mappers.getMapper(ExchangeTransactionMapper.class);

    ExchangeTransactionDto toDto(ExchangeTransaction transaction);

    List<ExchangeTransactionDto> toDtoList(List<ExchangeTransaction> transactions);
}
