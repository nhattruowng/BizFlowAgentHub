# Gateway API Module

## Muc tieu
- Lam diem vao chuan cho task intake tren Spring WebFlux.
- Chuan hoa tenant ID truoc khi ghi vao Postgres/R2DBC.
- Cung cap API de tao, liet ke va tra cuu chi tiet task.

## Kien truc chinh
- `TaskController`: REST entrypoint cho `/api/tasks`.
- `TaskService`: xu ly tao task, luu input payload, goi `workflow-engine`.
- `WorkflowClient`: WebClient reactive de kick-off workflow run.
- `TenantIdentifierResolver`: hop nhat alias tenant va UUID canon.
- `TenantContextFilter`: bo sung header mac dinh cho local/dev flow.

## Nang cap da hoan thien
- Ho tro alias `demo-tenant` va `demo`, map ve tenant UUID seed `11111111-1111-1111-1111-111111111111`.
- Them `GET /api/tasks` de list task theo `tenantId` va `limit`.
- Them `GET /api/tasks/{id}/details` de xem metadata + payload da luu.
- Sua validation error response de de doc hon o muc field-level.
- Thay smoke test bang service test cho create success, workflow failure va payload retrieval.

## API chinh
### `POST /api/tasks`
```json
{
  "tenantId": "demo-tenant",
  "source": "email",
  "type": "email_support",
  "payload": {
    "subject": "Login issue"
  },
  "correlationId": "corr-123"
}
```

### `GET /api/tasks?tenantId=demo-tenant&limit=20`
- Tra ve danh sach task moi nhat cua tenant.

### `GET /api/tasks/{id}/details`
- Tra ve `tenantId`, `source`, `type`, `workflowName`, `status`, `payload`, timestamps.

## Luu y van hanh
- Header mac dinh `X-Tenant-Id` da duoc chuyen sang UUID seed hop le thay vi chuoi `demo-tenant`.
- Neu client gui tenant khong phai UUID va khong nam trong danh sach alias ho tro, API tra `400 Bad Request`.
- `workflow-engine` van la dependency bat buoc trong luong `create task`.
