package com.example.atom.services;

import com.example.atom.dto.PriorityDto;
import com.example.atom.dto.ProductionPlanDto;
import com.example.atom.dto.ProductionPlanStatus;
import com.example.atom.entities.ProductionPlan;
import com.example.atom.repositories.ProductionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionPlanService {

    private final ProductionPlanRepository productionPlanRepository;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Long getCurrentMaxPriority() {
        //find max property from
        String query = "select max(priority) as priority from production_plan";
        return getCurrentPriority(query);
    }

    public Long getCurrentMaxApprovedOrInProductionPriority() {
        //find max property from
        String query = "select max(priority) as priority from production_plan where production_plan_status in ('APPROVED', 'IN_PRODUCTION')";
        return getCurrentPriority(query);
    }

    public Long getCurrentPriority(String query) {

        List<PriorityDto> maxPriority = namedParameterJdbcTemplate.query(
                query, new BeanPropertyRowMapper<>(PriorityDto.class, false));

        if (maxPriority.isEmpty()) {
            return 0L;
        } else {
            return maxPriority.get(0) == null ||  maxPriority.get(0).getPriority() == null
                    ? 0L : maxPriority.get(0).getPriority();
        }
    }

    @Transactional
    public void blankSave(List<ProductionPlanDto> dtos) {
        List<ProductionPlan> productionPlanList = new ArrayList<>();
        List<ProductionPlan> existsProductionPlans = productionPlanRepository
                .findAllById(dtos.stream().map(ProductionPlanDto::getId).toList());
        Map<Long, ProductionPlan> map = existsProductionPlans.stream().collect(
                Collectors.toMap(ProductionPlan::getId, Function.identity()));
        dtos.forEach(dto -> {
            ProductionPlan productionPlan = map.get(dto.getId());
            if(productionPlan != null) {
                productionPlan.setRequestId(dto.getRequestId());
                productionPlan.setPriority(dto.getPriority());
                productionPlan.setProductionPlanStatus(ProductionPlanStatus.BLANK);
            } else {
                productionPlan = new ProductionPlan();
                productionPlan.setId(dto.getId());
                productionPlan.setRequestId(dto.getRequestId());
                productionPlan.setPriority(dto.getPriority());
                productionPlan.setProductionPlanStatus(ProductionPlanStatus.BLANK);
                productionPlanList.add(productionPlan);
            }
        });
        productionPlanRepository.saveAllAndFlush(productionPlanList);
    }

    @Transactional
    public void approvePlan(Long planId, List<ProductionPlanDto> dtos) {
        List<ProductionPlan> productionPlanList = new ArrayList<>();
        //approve - переводим из blank в approve какую-нибудь существующую позицию плана
        ProductionPlan productionPlanToApprove = productionPlanRepository.findById(planId).orElse(null);
        ProductionPlan newProductionPlanToApprove = new ProductionPlan();
        Boolean approveWasNew = false;
        ProductionPlanDto dtoToApprove = dtos.stream().filter(e ->
                Optional.ofNullable(e.getId()).orElse(-111L).equals(planId)).toList().get(0);
        Long startPriorityOfApproved = dtoToApprove.getPriority();
        if (productionPlanToApprove != null) { //в списке blank'в уже была та позиция, которую апрувим
            productionPlanToApprove.setProductionPlanStatus(ProductionPlanStatus.APPROVED);
            productionPlanToApprove.setPriority(getCurrentMaxApprovedOrInProductionPriority() + 1);
            productionPlanRepository.saveAndFlush(productionPlanToApprove); //сохранили, чтобы снизу это не попало в бланки
        } else { //в списке blank'ов её не было, вообще такую позицию видим впервые
            approveWasNew = true;
            newProductionPlanToApprove.setRequestId(dtoToApprove.getRequestId());
            newProductionPlanToApprove.setProductionPlanStatus(ProductionPlanStatus.APPROVED);
            newProductionPlanToApprove.setPriority(getCurrentMaxApprovedOrInProductionPriority() + 1);
            productionPlanRepository.saveAndFlush(newProductionPlanToApprove); //сохранили, чтобы снизу это не попало в бланки
        }
        List<ProductionPlan> allProductionPlans = productionPlanRepository.findAll();
        List<ProductionPlan> blankPlans = allProductionPlans.stream().filter(e ->
                e.getProductionPlanStatus().equals(ProductionPlanStatus.BLANK)).toList();
//        productionPlanRepository.deleteAll(blankPlans);
//        productionPlanRepository.flush();
        Map<Long, ProductionPlan> mapBlankByRequestId = blankPlans.stream()
                .collect(Collectors.toMap(ProductionPlan::getRequestId, Function.identity()));
        for(ProductionPlanDto dto : dtos) {
//            if(productionPlanToApprove != null && productionPlanToApprove.getRequestId() != null
//                    && !dto.getRequestId().equals(productionPlanToApprove.getRequestId())) {
//                ProductionPlan productionPlan = new ProductionPlan();
//                productionPlan.setRequestId(dto.getRequestId());
//                productionPlan.setPriority(dto.getPriority() + getCurrentMaxApprovedPriority() + 1);
//                productionPlan.setProductionPlanStatus(ProductionPlanStatus.BLANK);
//                productionPlanList.add(productionPlan);
//            }
            //если к нам с фронта пришли 5 новых blank и 10 старых blank, то у десяти штук я пересетил priority - так как они
            //могли быть перебиты руками и плюс сверху мы апрувнули одну позицию, новым проставим весь entity
            if (approveWasNew && !dto.getRequestId().equals(newProductionPlanToApprove.getRequestId())
            ||
                    (!approveWasNew && productionPlanToApprove != null &&
                    !dto.getRequestId().equals(productionPlanToApprove.getRequestId()))) {
                ProductionPlan productionPlanBlank = mapBlankByRequestId.get(dto.getRequestId());
                if (productionPlanBlank != null) { //к нам с фронта пришел старый бланк
                    productionPlanBlank.setPriority(dto.getPriority()
                            + ((dto.getPriority() > startPriorityOfApproved) ? 0 : 1));
                } else { //это новый blank
                    ProductionPlan productionPlan = new ProductionPlan();
                    productionPlan.setRequestId(dto.getRequestId());
                    productionPlan.setPriority(dto.getPriority()
                            + ((dto.getPriority() > startPriorityOfApproved) ? 0 : 1));
                    productionPlan.setProductionPlanStatus(ProductionPlanStatus.BLANK);
                    productionPlanList.add(productionPlan);
                }
            }

        };
        productionPlanRepository.saveAllAndFlush(productionPlanList);
    }


}
