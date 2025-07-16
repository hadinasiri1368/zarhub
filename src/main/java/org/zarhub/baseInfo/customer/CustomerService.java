package org.zarhub.baseInfo.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zarhub.authentication.user.UserService;
import org.zarhub.authentication.user.dto.UserGroupDetailDto;
import org.zarhub.baseInfo.customer.constant.UserGroupType;
import org.zarhub.baseInfo.customer.dto.CustomerDto;
import org.zarhub.common.Utils;
import org.zarhub.model.Customer;
import org.zarhub.model.UserGroup;
import org.zarhub.model.UserGroupDetail;
import org.zarhub.model.Users;
import org.zarhub.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    private final JpaRepository repository;
    private final UserService userService;

    public CustomerService(JpaRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public void insert(Customer customer, Long userId, String uuid) throws Exception {
        if (!Utils.isNull(customer.getPerson()))
            repository.save(customer.getPerson(), userId, uuid);
        repository.save(customer, userId, uuid);
        Users users = new Users(null, true, false, customer.getPerson(), customer.getPerson().getNationalCode(), Utils.encodePassword(customer.getPerson().getNationalCode()));
        userService.insert(users, userId, uuid);
        userService.assignUserToGroup(users.getId(), UserGroupType.CUSTOMER_GROUP.getId(), userId, uuid);
    }

    public void delete(Long customerId, Long userId, String uuid) throws Exception {
        Customer customer = repository.findOne(Customer.class, customerId);
        repository.remove(customer.getPerson(), userId, uuid);
        repository.remove(customer, userId, uuid);
    }

    public void update(Customer customer, Long userId, String uuid) throws Exception {
        if (!Utils.isNull(customer.getPerson()) && !Utils.isNull(customer.getPerson().getId()))
            repository.update(customer.getPerson(), userId, uuid);
        repository.update(customer, userId, uuid);
    }

    public List<Customer> list(Long id) {
        if (Utils.isNull(id))
            return repository.findAll(Customer.class);
        return repository.findAll(Customer.class).stream()
                .filter(a -> a.getId().equals(id)).toList();
    }

    public Page<CustomerDto> listDto(int page, int size) {
        List<CustomerDto> list = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerList = repository.findByPage(Customer.class, pageable);
        for (Customer customer : customerList.getContent()) {
            list.add(customer.toDto());
        }
        return new PageImpl<>(list, pageable, customerList.getTotalElements());
    }
}
