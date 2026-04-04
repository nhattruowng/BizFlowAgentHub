# Approval Service Module

## Muc tieu
- Quan ly approval queue reactive cho cac thao tac nhay cam.
- Cung cap API de tao, truy van va quyet dinh approval mot cach an toan.

## Nang cap da hoan thien
- Them `GET /api/approvals/{id}` de doc chi tiet approval.
- `GET /api/approvals` ho tro filter theo `workflowRunId` va `status`.
- Chan approve/reject lap lai tren approval da duoc quyet dinh, tra `409 Conflict`.
- `ApprovalResponse` bo sung `updatedAt`.
- Them outbox relay va Kafka event cho `approval.requested`, `approval.approved`, `approval.rejected`.
- Thay context test bang service test cho create, conflict va filter list.

## API chinh
### `GET /api/approvals?workflowRunId=run-3&status=PENDING`
- Tra ve danh sach approval theo filter.

### `GET /api/approvals/{id}`
- Tra ve mot approval cu the.

### `POST /api/approvals/{id}/approve`
### `POST /api/approvals/{id}/reject`
- Yeu cau header `X-User-Id` de ghi nhan nguoi ra quyet dinh.

## Luu y thiet ke
- Chi approval o trang thai `PENDING` moi duoc phep chuyen sang `APPROVED` hoac `REJECTED`.
- Muc conflict duoc dua len API thay vi silently overwrite de tranh mat audit trail.
- Approval event duoc publish bat dong bo qua Kafka topic `events` de workflow-engine va audit-service consume.
