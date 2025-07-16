package org.zarhub.baseInfo.customer;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zarhub.baseInfo.customer.dto.CustomerDto;
import org.zarhub.common.Utils;
import org.zarhub.config.request.RequestContext;
import org.zarhub.constant.Consts;
import org.zarhub.dto.GenericDtoMapper;
import org.zarhub.model.Customer;
import org.zarhub.validator.NotEmpty;
import org.zarhub.validator.ValidateField;

import java.util.List;

@RestController
@Validated
public class CustomerController {
    private final CustomerService service;
    private final GenericDtoMapper mapper;

    public CustomerController(CustomerService customerService, GenericDtoMapper mapper) {
        this.service = customerService;
        this.mapper = mapper;
    }

    @PostMapping(path = Consts.DEFAULT_VERSION_API_URL + "/baseInfo/customer/add")
    public void insert(@RequestBody CustomerDto customerDto) throws Exception {
        service.insert(mapper.toEntity(Customer.class, customerDto), RequestContext.getUserId(), RequestContext.getUuid());
    }

    @PutMapping(path = Consts.DEFAULT_VERSION_API_URL + "/baseInfo/customer/edit")
    public void edit(@RequestBody CustomerDto customerDto) throws Exception {
        service.update(mapper.toEntity(Customer.class, customerDto), RequestContext.getUserId(), RequestContext.getUuid());
    }

    @DeleteMapping(path = Consts.DEFAULT_VERSION_API_URL + "/baseInfo/customer/remove/{id}")
    public void remove(@PathVariable @NotEmpty(fieldName = "id")
                       @ValidateField(fieldName = "id", entityClass = Customer.class) Long customerId) throws Exception {
        service.delete(customerId, RequestContext.getUserId(), RequestContext.getUuid());
    }

    @GetMapping(path = Consts.DEFAULT_VERSION_API_URL + "/baseInfo/customer/{id}")
    public CustomerDto getList(@PathVariable(value = "id") @ValidateField(fieldName = "id", entityClass = Customer.class) Long customerId) {
        List<Customer> customers = service.list(customerId);
        return !Utils.isNull(customers) ? customers.get(0).toDto() : null;
    }

    @GetMapping(path = Consts.DEFAULT_VERSION_API_URL + "/baseInfo/customer")
    public Page<CustomerDto> getCustomerList(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        return service.listDto(page, size);
    }
}
