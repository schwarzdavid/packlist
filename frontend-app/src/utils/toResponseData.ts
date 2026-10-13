import type {AxiosResponse} from "axios";

export function toResponseData<T>(this: void, response: AxiosResponse<T>): T {
  return response.data
}
