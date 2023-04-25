package com.canse.banking.services;

import com.canse.banking.dto.ContactDto;
import com.canse.banking.models.Contact;

import java.util.List;

public interface ContactService extends AbstractService<ContactDto>{

    List<ContactDto> findAllByUserId(Integer userId);

}
