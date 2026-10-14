import {AxiosError, AxiosHeaders, HttpStatusCode} from "axios";
import type {ProblemDetails} from "@packlist/users-api";

export function createAxiosUnauthorizedError(): AxiosError<ProblemDetails> {
  return new AxiosError<ProblemDetails>("Request failed with status code 401", AxiosError.ERR_BAD_REQUEST, undefined, undefined, {
    status: HttpStatusCode.Unauthorized,
    statusText: "Unauthorized",
    data: {status: HttpStatusCode.Unauthorized, title: 'Unauthorized'},
    headers: {},
    config: {headers: new AxiosHeaders()}
  })
}

export function createAxiosInternalServerError(): AxiosError<ProblemDetails> {
  return new AxiosError<ProblemDetails>("Request failed with status code 500", AxiosError.ERR_BAD_RESPONSE, undefined, undefined, {
    status: HttpStatusCode.InternalServerError,
    statusText: "Unauthorized",
    data: {status: HttpStatusCode.InternalServerError, title: 'Internal Server Error'},
    headers: {},
    config: {headers: new AxiosHeaders()}
  })
}

export function createAxiosConflictError(field: string): AxiosError<ProblemDetails> {
  return new AxiosError("Request failed with status code 409", AxiosError.ERR_BAD_REQUEST, undefined, undefined, {
    status: HttpStatusCode.Conflict,
    statusText: "Conflict",
    data: {
      title: 'Conflict',
      status: HttpStatusCode.Conflict,
      fields: {
        [field]: {
          error: `validation.${field}Taken`
        }
      }
    } satisfies ProblemDetails,
    headers: {},
    config: {headers: new AxiosHeaders()}
  })
}
