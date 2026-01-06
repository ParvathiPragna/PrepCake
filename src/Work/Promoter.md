Here’s a concise and structured summary of the **Sky Image Promoter** project:

---

### 🚀 **Sky Image Promoter – Project Overview**

**Purpose:**  
Automates the lifecycle of raw image files into validated, labeled, and reusable cloud images across multiple cloud providers using an event-driven architecture.

---

### 🧩 **Core Workflow**

1. **Ingestion & Metadata Capture**
    - Accepts raw image files along with metadata: provider, checksums, image name, etc.
    - Creates a corresponding document in **MongoDB** for tracking.

2. **Event-Driven Upload & Import**
    - Uses **Spring Framework** with annotations like `@EventListener`, `@Component`, and `@Repository`.
    - Triggers asynchronous events to:
        - Upload the file to **GCP**, **AWS**, or **OCI** using **multipart upload**.
        - Import the uploaded file as a cloud image.

3. **Validation Pipeline**
    - **Basic Tests:** Automatically spins up a test instance using the image to validate boot and basic functionality.
    - **Advanced Tests:** If basic tests pass, deeper validation suites are triggered.
    - **Failure Handling:** On any failure (image creation or test), an **email alert** is sent to the user.

4. **Post-Validation Actions**
    - Once all tests pass:
        - User can **apply a label** to the image.
        - Labeled images become **discoverable and reusable** by other users to launch new instances.

---

### ☁️ **Cloud & Tech Stack**

| Component              | Technology Used                          |
|------------------------|-------------------------------------------|
| Backend Framework      | Spring (with `@EventListener`, etc.)     |
| Database               | MongoDB                                  |
| Cloud Providers        | AWS, GCP, OCI                            |
| Upload Mechanism       | Multipart Upload                         |
| Architecture Style     | Event-Driven                             |
| Notification           | Email Alerts                             |

---

![](C:\Users\parva\Pictures\Architecture diagram.png)