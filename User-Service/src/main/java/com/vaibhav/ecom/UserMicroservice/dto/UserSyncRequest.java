package com.vaibhav.ecom.UserMicroservice.dto;

import lombok.Data;

@Data
public class UserSyncRequest {
	private String email;
	private String username;
}
