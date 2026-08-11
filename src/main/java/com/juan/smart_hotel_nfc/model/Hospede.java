package com.juan.smart_hotel_nfc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hospede")
public class Hospede extends Pessoa {





}
