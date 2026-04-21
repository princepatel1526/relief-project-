# Disaster Relief Platform — Demo Login Credentials

All demo accounts use the password: **password123**

> Admin bootstrapped by AdminUserInitializer uses password: **Admin@2024**
> (Email: admin@example.com — this is separate from the seeded admin below)

---

## Super Admin
| Username     | Email                              | Password    |
|-------------|-------------------------------------|-------------|
| superadmin  | superadmin@disasterrelief.org       | password123 |

**Dashboard:** Admin Dashboard — full access to all platform features.

---

## Admins
| Username | Email                        | Password    |
|---------|-------------------------------|-------------|
| admin   | admin@disasterrelief.org      | password123 |
| admin2  | admin2@disasterrelief.org     | password123 |

**Dashboard:** Admin Dashboard — operational and configuration access.

---

## NGO Coordinators
| Username          | Email                              | Password    |
|------------------|------------------------------------|-------------|
| ops_lead_mumbai  | ops.mumbai@disasterrelief.org      | password123 |
| ops_lead_chennai | ops.chennai@disasterrelief.org     | password123 |
| ops_lead_delhi   | ops.delhi@disasterrelief.org       | password123 |

**Dashboard:** Admin Dashboard (coordinator view) — inventory, volunteers, news publishing.

---

## Responders
| Username     | Email                  | Password    | Profile                     |
|-------------|------------------------|-------------|-----------------------------|
| responder_01 | resp01@ndrf.gov.in    | password123 | NDRF Search & Rescue        |
| responder_02 | resp02@ndrf.gov.in    | password123 | Fire Response / Hazmat      |
| responder_03 | resp03@ndrf.gov.in    | password123 | Trauma Care / Medical       |
| responder_04 | resp04@sdrf.gov.in    | password123 | Urban SAR / Structural      |

**Dashboard:** Role Dashboard (Responder view) — priority queue, emergency assignments.

---

## Volunteers
| Username          | Email                           | Password    | Location    |
|------------------|---------------------------------|-------------|-------------|
| volunteer_mum_01 | vol.mum01@disasterrelief.org    | password123 | Mumbai      |
| volunteer_mum_02 | vol.mum02@disasterrelief.org    | password123 | Thane       |
| volunteer_che_01 | vol.che01@disasterrelief.org    | password123 | Chennai     |
| volunteer_asm_01 | vol.asm01@disasterrelief.org    | password123 | Guwahati    |
| volunteer_del_01 | vol.del01@disasterrelief.org    | password123 | Delhi       |
| volunteer_kol_01 | vol.kol01@disasterrelief.org    | password123 | Kolkata     |
| volunteer_pun_01 | vol.pun01@disasterrelief.org    | password123 | Chandigarh  |
| volunteer_hyd_01 | vol.hyd01@disasterrelief.org    | password123 | Hyderabad   |

**Dashboard:** Role Dashboard (Volunteer view) — assigned tasks, nearby incidents.

---

## Citizens
| Username              | Email                         | Password    | City       |
|----------------------|-------------------------------|-------------|------------|
| citizen_mumbai_01    | citizen.mum01@example.com     | password123 | Mumbai     |
| citizen_chennai_01   | citizen.che01@example.com     | password123 | Chennai    |
| citizen_kolkata_01   | citizen.kol01@example.com     | password123 | Kolkata    |
| citizen_delhi_01     | citizen.del01@example.com     | password123 | Delhi      |
| citizen_delhi_02     | citizen.del02@example.com     | password123 | Delhi      |
| citizen_pune_01      | citizen.pun01@example.com     | password123 | Pune       |
| citizen_pune_02      | citizen.pun02@example.com     | password123 | Pune       |
| citizen_hyderabad_01 | citizen.hyd01@example.com     | password123 | Hyderabad  |
| citizen_bengaluru_01 | citizen.blr01@example.com     | password123 | Bengaluru  |
| citizen_guwahati_01  | citizen.guw01@example.com     | password123 | Guwahati   |

**Dashboard:** Role Dashboard (Citizen view) — report incidents, view news, track requests.

---

## Donors (mapped to Citizen role)
| Username     | Email                        | Password    | Name                      |
|-------------|------------------------------|-------------|---------------------------|
| donor_corp_01 | donor.corp01@example.com   | password123 | Suryanet Logistics Pvt Ltd|
| donor_ind_01  | donor.ind01@example.com    | password123 | Neha Kulkarni             |
| donor_corp_02 | donor.corp02@example.com   | password123 | Bharat Tech Solutions     |
| donor_corp_03 | donor.corp03@example.com   | password123 | Greenpath Foundation      |
| donor_ind_02  | donor.ind02@example.com    | password123 | Arjun Singhvi             |

**Dashboard:** Role Dashboard (Citizen view). Donors have existing payment/donation records.

---

## Running the Seed

```bash
mysql -u root -p disaster_relief_db < backend/src/main/resources/db/seed_demo.sql
```

The seed is idempotent — safe to run multiple times. All inserts use
`WHERE NOT EXISTS` guards to prevent duplicates.
