# Workflow Engine Module

## Muc tieu
- Dieu phoi workflow theo mo hinh reactive, ro rang va co the truy vet.
- Dinh nghia workflow theo catalog thay vi auto-register moi gia tri tu do.
- Tra ve chi tiet run va tung step de UI va ops de quan sat.

## Nang cap da hoan thien
- Them `WorkflowCatalog` cho 3 workflow MVP: `email-ticket-workflow`, `invoice-approval-workflow`, `policy-lookup-workflow`.
- Tu choi workflow khong nam trong catalog bang `404`, tranh tao workflow rac trong DB.
- Mo rong `WorkflowRunResponse` voi `currentStep`, `errorReason`, `stepDetails`.
- Them `GET /api/workflows` de client co the discover danh sach workflow duoc ho tro.
- Thay context test bang service test cho run waiting-approval, reject unknown workflow va read-back step details.

## API chinh
### `GET /api/workflows`
- Liet ke workflow name, mo ta, terminal status va danh sach step.

### `POST /api/workflows/run`
```json
{
  "workflowName": "invoice-approval-workflow",
  "tenantId": "11111111-1111-1111-1111-111111111111",
  "taskId": "task-1",
  "correlationId": "corr-1",
  "input": {
    "amount": 1500
  }
}
```

### `GET /api/workflows/runs/{id}`
- Tra ve thong tin run va chi tiet tung step reactive da persist.

## Luu y thiet ke
- `WAITING_APPROVAL` duoc model hoa thanh step cuoi voi `endedAt = null`.
- Catalog la noi duy nhat de khai bao workflow duoc support trong MVP.
- Neu can them workflow moi, uu tien cap nhat `WorkflowCatalog` truoc khi mo endpoint intake o layer khac.
