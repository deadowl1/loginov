
from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel

app = FastAPI(
    title="Service Desk API",
    description="REST API для обработки обращений в службу технической поддержки",
    version="1.0.0",
)


# Модель данных для создания обращения
class RouteCreate(BaseModel):
    client_name: str
    description: str
    category: str
    priority: str
    status: str = "Новая"


# Модель данных обращения с идентификатором
class Route(RouteCreate):
    id: int


# Хранение обращений в памяти
routes = {}
next_id = 1

@app.get("/")
def home():
    return{}

# GET — получение списка обращений
@app.get(
    "/api/v1/routes/",
    response_model=list[Route],
    responses={
        400: {"description": "Некорректный запрос"}
    },
)
def get_routes():
    return list(routes.values())


# GET — получение одного обращения
@app.get(
    "/api/v1/routes/{route_id}",
    response_model=Route,
    responses={
        404: {"description": "Обращение не найдено"}
    },
)
def get_route(route_id: int):
    if route_id not in routes:
        raise HTTPException(
            status_code=404,
            detail="Обращение не найдено"
        )

    return routes[route_id]


# POST — создание обращения
@app.post(
    "/api/v1/routes/",
    response_model=Route,
    status_code=status.HTTP_201_CREATED,
    responses={
        400: {"description": "Некорректный запрос"}
    },
)
def create_route(route: RouteCreate):
    global next_id

    new_route = Route(
        id=next_id,
        **route.model_dump()
    )

    routes[next_id] = new_route
    next_id += 1

    return new_route


# DELETE — удаление обращения
@app.delete(
    "/api/v1/routes/{route_id}",
    status_code=status.HTTP_200_OK,
    responses={
        404: {"description": "Обращение не найдено"}
    },
)
def delete_route(route_id: int):
    if route_id not in routes:
        raise HTTPException(
            status_code=404,
            detail="Обращение не найдено"
        )

    del routes[route_id]

    return {
        "message": "Обращение удалено"
    }
