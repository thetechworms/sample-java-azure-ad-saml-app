# Java SAML SSO with Azure AD

A minimal **Java 17 / Spring Boot 3.2** application that implements **SAML 2.0 Single Sign-On** using **Azure Active Directory** as the Identity Provider.

## How It Works

```
Browser → Spring Boot App → Azure AD (SAML AuthnRequest)
                         ← Azure AD (SAML Response with user attributes)
Browser ← Spring Boot App (authenticated session)
```

## Prerequisites

- Java 17+
- Maven 3.8+
- An Azure account with an Azure AD tenant

---

## Step 1 — Configure Azure AD Enterprise Application

1. Go to [Azure Portal](https://portal.azure.com) → **Azure Active Directory** → **Enterprise applications**
2. Click **New application** → **Create your own application**
3. Give it a name (e.g. `java-saml-demo`) → select **"Integrate any other application you don't find in the gallery (Non-gallery)"** → **Create**
4. In the app, go to **Single sign-on** → select **SAML**
5. Click **Edit** on **Basic SAML Configuration** and set:

   | Field | Value |
   |---|---|
   | **Identifier (Entity ID)** | Any unique URI, e.g. `https://localhost:8080/saml` |
   | **Reply URL (ACS URL)** | `http://localhost:8080/login/saml2/sso/azure` |
   | **Sign on URL** | `http://localhost:8080/` |

6. Save the configuration
7. Copy the **App Federation Metadata URL** (shown in section 3 of the SAML setup page)
8. **Assign users/groups** to the app (Users and groups → Add user/group)

---

## Step 2 — Configure the Application

Edit `src/main/resources/application.yml` and fill in your values:

```yaml
saml2:
  azure:
    # Must match the "Identifier (Entity ID)" you set in Azure AD
    entity-id: "https://localhost:8080/saml"

    # App Federation Metadata URL from Azure AD SAML setup page
    metadata-uri: "https://login.microsoftonline.com/{tenant-id}/federationmetadata/2007-06/federationmetadata.xml?appid={app-id}"
```

---

## Step 3 — Run the Application

```bash
mvn spring-boot:run
```

Then open your browser at: **http://localhost:8080/**

---

## Login Flow

1. Click **"Sign in with Azure AD"** on the home page
2. You'll be redirected to Microsoft's login page
3. Sign in with your Azure AD credentials
4. You'll be redirected back to `/profile` showing your name and SAML attributes

---

## Project Structure

```
src/main/java/com/example/saml/
├── SamlApplication.java     # Spring Boot entry point
├── SecurityConfig.java      # SAML2 SP configuration
└── HomeController.java      # Home + profile pages

src/main/resources/
├── application.yml          # App config (fill in your Azure AD values)
└── templates/
    ├── home.html            # Home / login page
    └── profile.html         # Authenticated user profile page
```

---

## Key Endpoints

| Endpoint | Description |
|---|---|
| `GET /` | Home page |
| `GET /profile` | Authenticated user profile (SAML attributes) |
| `GET /saml2/authenticate/azure` | Initiates SAML login with Azure AD |
| `POST /login/saml2/sso/azure` | ACS URL — Azure AD posts SAML response here |
| `POST /logout` | Logs out the user |

---

## Troubleshooting

**"AADSTS750054: SAMLRequest or SAMLResponse is not present"**
→ Make sure the Reply URL in Azure AD exactly matches `http://localhost:8080/login/saml2/sso/azure`

**"No relying party registration found"**
→ Check that `metadata-uri` in `application.yml` is the correct App Federation Metadata URL

**User not assigned to the application**
→ In Azure AD, go to Enterprise Applications → your app → Users and groups → assign the user

