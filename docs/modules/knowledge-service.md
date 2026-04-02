# Knowledge Service Module

## Muc tieu
- Cung cap search reactive cho tai lieu noi bo va boi canh doanh nghiep.
- Ho tro truy van nhanh, co scoring de UI/agent uu tien ket qua dung hon.

## Nang cap da hoan thien
- Them `GET /api/knowledge/documents` de liet ke knowledge docs.
- `KnowledgeSearchResult` bo sung `score`.
- Search duoc sap xep lai theo diem uu tien title/source/content thay vi chi theo chunk index.
- Gioi han `limit` duoc chuan hoa trong khoang hop ly cho MVP.
- Context test duoc thay bang service test cho sort doc va score result.

## API chinh
### `GET /api/knowledge/documents`
- Liet ke danh muc tai lieu tri thuc hien co.

### `POST /api/knowledge/search`
```json
{
  "query": "invoice",
  "limit": 5
}
```

## Luu y thiet ke
- He thong scoring hien tai uu tien title match truoc, sau do moi den source va content match.
- Day la buoc chuyen tu search MVP sang search co ranking, de sau nay de nang cap vector search hon.
