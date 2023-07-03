package com.thoroldvix.economatic.goldprice.mapper;

import com.thoroldvix.economatic.goldprice.dto.GoldPriceListResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPricePageResponse;
import com.thoroldvix.economatic.goldprice.dto.GoldPriceResponse;
import com.thoroldvix.economatic.goldprice.model.GoldPrice;
import com.thoroldvix.economatic.server.model.Server;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.thoroldvix.economatic.shared.util.ValidationUtils.checkNullAndGet;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GoldPriceMapper {

    default GoldPriceListResponse toGoldPriceList(List<GoldPrice> prices) {
        return new GoldPriceListResponse(toList(prices));
    }

    default GoldPricePageResponse toPageResponse(Page<GoldPrice> page) {
        List<GoldPriceResponse> prices = toList(page.getContent());
        return new GoldPricePageResponse(new PaginationInfo(page), prices);
    }

    @Mapping(target = "price", source = "value")
    @Mapping(target = "server", source = "server", qualifiedByName = "serverName")
    GoldPriceResponse toResponse(GoldPrice goldPrice);


    List<GoldPriceResponse> toList(List<GoldPrice> prices);

    @Named("serverName")
    default String serverName(Server server) {
        return checkNullAndGet(server::getUniqueName);
    }
}