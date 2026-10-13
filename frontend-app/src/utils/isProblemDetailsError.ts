import type {ProblemDetails} from "@packlist/users-api";
import {type AxiosError, isAxiosError} from "axios";

export function isProblemDetailsError(err: unknown): err is AxiosError<ProblemDetails> {
  if (!isAxiosError(err) || !err.response) {
    return false;
  }
  return "status" in err.response.data;
}
